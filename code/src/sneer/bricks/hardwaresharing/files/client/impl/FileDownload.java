package sneer.bricks.hardwaresharing.files.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.hardwaresharing.files.protocol.FileContents;
import sneer.bricks.hardwaresharing.files.protocol.FileContentsFirstBlock;
import sneer.bricks.hardwaresharing.files.protocol.FileRequest;
import sneer.bricks.hardwaresharing.files.protocol.Protocol;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.brickness.Tuple;
import sneer.foundation.lang.Consumer;

class FileDownload extends AbstractDownload {

	private static final int MAX_BLOCKS_DOWNLOADED_AHEAD = 100;

	private OutputStream _output;
	private final List<FileContents> _blocksToWrite = new ArrayList<FileContents>();
	private int _nextBlockToWrite = 0;
	private int _fileSizeInBlocks = -1;

	private long _lastRequestTime = -1;

	@SuppressWarnings("unused") private WeakContract _fileContentConsumerContract;


	FileDownload(File file, long lastModified, Sneer1024 hashOfFile) {
		this(file, lastModified, hashOfFile, null);
	}


	FileDownload(File file, long lastModified, Sneer1024 hashOfFile, Runnable toCallWhenFinished) {
		super(file, lastModified, hashOfFile, toCallWhenFinished);

		if (isFinished()) return; 
		subscribeToFileContents();
		startSendingRequests();
	}


	private void subscribeToFileContents() {
		_fileContentConsumerContract = my(TupleSpace.class).addSubscription(FileContents.class, new Consumer<FileContents>() { @Override public void consume(FileContents contents) {
			receiveFileBlock(contents);
		}});
	}

	
	synchronized
	private void receiveFileBlock(FileContents contents) {
		if (isFinished()) return;

		try {
			tryToReceiveFileBlock(contents);
		} catch (IOException ioe) {
			finishWith(ioe);
		}
	}

	
	private void tryToReceiveFileBlock(FileContents contents) throws IOException {
		if (!contents.hashOfFile.equals(_hash)) return;

		my(Logger.class).log("File block received. File: {}, Block: ", contents.debugInfo, contents.blockNumber);

		if (contents instanceof FileContentsFirstBlock)
			receiveFirstBlock((FileContentsFirstBlock) contents);

		if (contents.blockNumber < _nextBlockToWrite) return;
		if (contents.blockNumber - _nextBlockToWrite > MAX_BLOCKS_DOWNLOADED_AHEAD) return; 

		_blocksToWrite.add(contents);
		tryToWriteBlocksInSequence();
	}

	
	private void receiveFirstBlock(FileContentsFirstBlock contents) throws IOException {
		if (firstBlockWasAlreadyReceived()) return;
		_fileSizeInBlocks = calculateFileSizeInBlocks(contents.fileSize);
		_output = new FileOutputStream(_path);
	}

	
	private boolean firstBlockWasAlreadyReceived() {
		return _fileSizeInBlocks != -1;
	} 

	
	private int calculateFileSizeInBlocks(long fileSizeInBytes) {
		final int result = (int) fileSizeInBytes / Protocol.FILE_BLOCK_SIZE;
		return (result != 0 && fileSizeInBytes % Protocol.FILE_BLOCK_SIZE != 0) ? result + 1 : result; 
	}

	
	private void tryToWriteBlocksInSequence() throws IOException {
		boolean written;
		do {
			written = false;
			Iterator<FileContents> it = _blocksToWrite.iterator();
			while(it.hasNext()) {
				FileContents block = it.next();
				if (block.blockNumber != _nextBlockToWrite) continue;
				it.remove();
				writeBlock(block.bytes.copy());
				written = true;
			}
		} while (written); // In case blocks have arrived out of order

		if (!isFinished()) publish(nextBlockRequest());
	}

	
	private void writeBlock(byte[] bytes) throws IOException {
		_output.write(bytes);
		++_nextBlockToWrite;
		if (readyToFinish()) {
			my(IO.class).streams().closeQuietly(_output);
			finishWithSuccess();
		}
	}


	private boolean readyToFinish() {
		return _nextBlockToWrite >= _fileSizeInBlocks;
	}

	
	@Override
	Tuple requestToPublishIfNecessary() {
		if (isFirstRequest())
			return nextBlockRequest();

		if (readyToFinish()) return null; //Might not have been finished yet.

		if (my(Clock.class).time().currentValue() - _lastRequestTime < REQUEST_INTERVAL)
			return null;
		
		return nextBlockRequest();
	}

	
	private boolean isFirstRequest() {
		return _lastRequestTime == -1;
	}

	
	private Tuple nextBlockRequest() {
		_lastRequestTime = my(Clock.class).time().currentValue();
		return new FileRequest(_hash, _nextBlockToWrite, _path.getAbsolutePath());
	}


	@Override
	void copyContents(Object contents) throws IOException {
		if (!(contents instanceof File)) throw new IOException("Wrong type of contents received. Should be File but was " + contents.getClass());
		my(IO.class).files().copyFile((File) contents, _path);
	}


	@Override
	Object mappedContentsBy(Sneer1024 hashOfContents) {
		return my(FileMap.class).getFile(hashOfContents);
	}

}

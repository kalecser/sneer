package sneer.bricks.expression.files.client.downloads.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FileContents;
import sneer.bricks.expression.files.protocol.FileContentsFirstBlock;
import sneer.bricks.expression.files.protocol.FileRequest;
import sneer.bricks.expression.files.protocol.Protocol;
import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.seals.Seal;
import sneer.foundation.lang.Consumer;

class FileDownload extends AbstractDownload {

	private static final int MAX_BLOCKS_DOWNLOADED_AHEAD = 100;

	private OutputStream _output;
	private final List<FileContents> _blocksToWrite = new ArrayList<FileContents>();
	private int _nextBlockToWrite = 0;
	private int _fileSizeInBlocks = -1;

	private long _lastRequestTime = -1;

	@SuppressWarnings("unused") private WeakContract _fileContentConsumerContract;


	FileDownload(File file, long lastModified, Hash hashOfFile) {
		this(file, lastModified, hashOfFile, null, null);
	}


	FileDownload(File file, long lastModified, Hash hashOfFile, Seal source, Runnable toCallWhenFinished) {
		super(file, lastModified, hashOfFile, source, toCallWhenFinished);

		start();
	}


	@Override
	void subscribeToContents() {
		_fileContentConsumerContract = my(RemoteTuples.class).addSubscription(FileContents.class, new Consumer<FileContents>() { @Override public void consume(FileContents contents) {
			receiveFileBlock(contents);
		}});
	}

	
	private void receiveFileBlock(FileContents contents) {
		registerActivity();

		if (isFinished()) return;

		try {
			tryToReceiveFileBlock(contents);
		} catch (IOException ioe) {
			finishWith(ioe);
		}
	}

	
	private void tryToReceiveFileBlock(FileContents contents) throws IOException {
		if (!contents.hashOfFile.equals(_hash)) return;

		if (contents instanceof FileContentsFirstBlock) {
			receiveFirstBlock((FileContentsFirstBlock) contents);
			if (isFinished()) return;  // Empty file case.
		}

		if (contents.blockNumber < _nextBlockToWrite) return;
		if (contents.blockNumber - _nextBlockToWrite > MAX_BLOCKS_DOWNLOADED_AHEAD) return; 

		my(Logger.class).log("File block received. File: {}, Block: ", contents.debugInfo, contents.blockNumber);

		_blocksToWrite.add(contents);
		setProgress((float) _nextBlockToWrite / _fileSizeInBlocks);
		tryToWriteBlocksInSequence();
	}

	
	private void receiveFirstBlock(FileContentsFirstBlock contents) throws IOException {
		if (firstBlockWasAlreadyReceived()) return;
		_fileSizeInBlocks = (contents.fileSize == 0) ? 0 : (int) ((contents.fileSize - 1) / Protocol.FILE_BLOCK_SIZE) + 1;
		if (_fileSizeInBlocks > 0)
			_output = new FileOutputStream(_path);			
		else
			finishWithSuccess();
	}

	
	private boolean firstBlockWasAlreadyReceived() {
		return _fileSizeInBlocks != -1;
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
			my(IO.class).crash(_output);
			finishWithSuccess();
		}
	}


	@Override
	void updateFileMapWith(File tmpFile, File actualFile) {
		my(FileMap.class).putFile(actualFile.getAbsolutePath(), actualFile.lastModified(), _hash);
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
		return new FileRequest(source(), _hash, _nextBlockToWrite, _path.getAbsolutePath());
	}


	@Override
	Object mappedContentsBy(Hash hashOfContents) {
		return my(FileMap.class).getFile(hashOfContents);
	}


	@Override
	void copyContents(Object contents) throws IOException {
		if (!(contents instanceof String)) throw new IOException("Wrong type of contents received. Should be String but was " + contents.getClass());
		my(IO.class).files().copyFile(new File((String)contents), _path);
	}

}

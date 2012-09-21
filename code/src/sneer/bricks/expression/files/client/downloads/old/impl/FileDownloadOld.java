package sneer.bricks.expression.files.client.downloads.old.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.protocol.FileContents;
import sneer.bricks.expression.files.protocol.FileRequest;
import sneer.bricks.expression.files.protocol.Protocol;
import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.seals.Seal;
import basis.lang.Consumer;
import basis.lang.arrays.ImmutableArray;

class FileDownloadOld extends AbstractDownloadOld {

	private static final int MAX_BLOCKS_DOWNLOADED_AHEAD = 1;

	private final int _fileSizeInBlocks;
	private final OutputStream _output;

	private final List<FileContents> _blocksToWrite = new ArrayList<FileContents>();
	private int _nextBlockToWrite = 0;
	private long _lastRequestTime = -1;

	@SuppressWarnings("unused") private WeakContract _fileContentConsumerContract;



	FileDownloadOld(File file, long size, long lastModified, Hash hashOfFile, Seal source, boolean copyLocalFiles) {
		super(file, lastModified, hashOfFile, source, copyLocalFiles);

		if (size == 0) {
			_fileSizeInBlocks = 0;
			_output = null;
			finishEmptyFile();
		} else {
			_fileSizeInBlocks = (int) ((size - 1) / Protocol.FILE_BLOCK_SIZE) + 1;
			_output = openOutputStream();
		}
		
		start();
	}


	private FileOutputStream openOutputStream() {
		try {
			recoverDownloadIfPossible();
			return new FileOutputStream(_path, true);
		} catch (IOException ioe) {
			finishWith(ioe);
			return null;
		}
	}


	private void finishEmptyFile() {
		try {
			if (!_path.exists())
				_path.createNewFile();
			finishCheckingHash();
		} catch (IOException ioe) {
			finishWith(ioe);
		}
	}


	@Override
	protected void subscribeToContents() {
		_fileContentConsumerContract = my(RemoteTuples.class).addSubscription(FileContents.class, new Consumer<FileContents>() { @Override public void consume(FileContents contents) {
			receiveFileBlock(contents);
		}});
	}

	
	private void receiveFileBlock(FileContents contents) {
		recordActivity();

		if (isFinished()) return;

		try {
			tryToReceiveFileBlock(contents);
		} catch (IOException ioe) {
			finishWith(ioe);
		}
	}

	
	private void tryToReceiveFileBlock(FileContents contents) throws IOException {
		if (!contents.hashOfFile.equals(_hash)) return;

		if (contents.blockNumber < _nextBlockToWrite) return;
		if (contents.blockNumber - _nextBlockToWrite > MAX_BLOCKS_DOWNLOADED_AHEAD) return; 

		my(Logger.class).log("File block received. File: {}, Block: ", contents.debugInfo, contents.blockNumber);

		_blocksToWrite.add(contents);
		tryToWriteBlocksInSequence();
		
		setProgress((float) _nextBlockToWrite / _fileSizeInBlocks);
	}

	
	private void recoverDownloadIfPossible() throws IOException {
		if (!_path.exists()) return;

		truncateFileToBlock();
		_nextBlockToWrite = (int) (_path.length() / Protocol.FILE_BLOCK_SIZE);
	}


	private void truncateFileToBlock() throws IOException {
		@SuppressWarnings("resource") //Channel delegates close to underlying stream
		FileChannel channel = new FileOutputStream(_path, true).getChannel();
		channel.truncate(_nextBlockToWrite * Protocol.FILE_BLOCK_SIZE);
		channel.close();
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
		_output.flush();
		++_nextBlockToWrite;
		if (readyToFinish()) {
			my(IO.class).crash(_output);
			finishCheckingHash();
		}
	}


	private void finishCheckingHash() throws IOException {
		if (!_hash.equals(my(Crypto.class).digest(_path)))
			throw new IOException("Downloaded file did not match its expected hash");

		finishWithSuccess();
	}


	@Override
	protected void updateFileMap() {
		my(FileMap.class).putFile(_actualPath.getAbsolutePath(), _actualPath.length(), _actualPath.lastModified(), _hash);
	}


	private boolean readyToFinish() {
		return _nextBlockToWrite == _fileSizeInBlocks;
	}

	
	@Override
	protected Collection<Tuple> requestsToPublishIfNecessary() {
		if (isFirstRequest())
			return initialBlockRequests();

		if (readyToFinish()) return Collections.EMPTY_LIST; //Might not have been finished yet.

		if (my(Clock.class).time().currentValue() - _lastRequestTime < REQUEST_INTERVAL)
			return Collections.EMPTY_LIST;
		
		return Arrays.asList(nextBlockRequest());
	}

	
	private boolean isFirstRequest() {
		return _lastRequestTime == -1;
	}

	
	private Collection<Tuple> initialBlockRequests() {
		_lastRequestTime = my(Clock.class).time().currentValue();
		List<Tuple> ret = new ArrayList<>(MAX_BLOCKS_DOWNLOADED_AHEAD);
		for (int i = 0; i <= MAX_BLOCKS_DOWNLOADED_AHEAD && i < _fileSizeInBlocks; i++)
			ret.add(requestForBlock(_nextBlockToWrite + i));
		return ret;
	}


	private Tuple nextBlockRequest() {
		_lastRequestTime = my(Clock.class).time().currentValue();
		int nextBlockRequest = _nextBlockToWrite;
		if (nextBlockRequest >= _fileSizeInBlocks) return null;
		return requestForBlock(nextBlockRequest);
	}


	private FileRequest requestForBlock(int number) {
		return new FileRequest(source(), _hash, new ImmutableArray<Integer>(number), _path.getAbsolutePath());
	}


	@Override
	protected File mappedContentsBy(Hash hashOfContents) throws FileNotFoundException {
		return my(FileMapper.class).getExistingMappedFile(hashOfContents);
	}


	@Override
	protected void finishWithLocalContents(Object pathToContents) throws IOException {
		my(IO.class).files().copyFile((File)pathToContents, _path);
		finishWithSuccess();
	}

	@Override
	protected boolean isWaitingForActivity() {
		return !isFinished();
	}

	@Override
	protected String getMappedPath(Hash hash) {
		try {
			return mappedContentsBy(hash).getAbsolutePath();
		} catch (FileNotFoundException e) {
			return null;
		}
	}

}

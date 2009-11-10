package sneer.bricks.hardwaresharing.files.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardwaresharing.files.hasher.Hasher;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.hardwaresharing.files.protocol.FileContents;
import sneer.bricks.hardwaresharing.files.protocol.FileContentsFirstBlock;
import sneer.bricks.hardwaresharing.files.protocol.FileRequest;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.hardwaresharing.files.protocol.Protocol;
import sneer.bricks.hardwaresharing.files.writer.AtomicFileWriter;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;

class Download {

	private static final int REQUEST_PERIOD = 15000;
	private final File _fileOrFolder;
	private final long _lastModified;
	private final Sneer1024 _hashOfContents;

	private OutputStream _output;
	private final List<FileContents> _blocksToWrite = new ArrayList<FileContents>();
	private int _nextBlockToWrite = 0;
	private int _fileSizeInBlocks = -1;

	private long _lastRequestTime = -1;

	private final Latch _isFinished = my(Latches.class).produce();
	private IOException _exception;

	@SuppressWarnings("unused") private final WeakContract _fileContract;
	@SuppressWarnings("unused") private final WeakContract _folderContract;
	@SuppressWarnings("unused") private final WeakContract _timerContract;


	Download(File fileOrFolder, long lastModified, Sneer1024 hashOfContents) {
		_fileOrFolder = fileOrFolder;
		_lastModified = lastModified;
		_hashOfContents = hashOfContents;

		checkRedundantDownload(fileOrFolder, hashOfContents);

		_fileContract = my(TupleSpace.class).addSubscription(FileContents.class, new Consumer<FileContents>() { @Override public void consume(FileContents contents) {
			receiveFileBlock(contents);
		}});
		
		_folderContract = my(TupleSpace.class).addSubscription(FolderContents.class, new Consumer<FolderContents>() { @Override public void consume(FolderContents contents) {
			receiveFolder(contents);
		}});
		
		_timerContract = my(Timer.class).wakeUpNowAndEvery(REQUEST_PERIOD, new Runnable() { @Override public void run() {
			requestNextBlockIfNecessary();
		}});
	}


	private void checkRedundantDownload(File fileOrFolder, Sneer1024 hashOfContents) {
		Object alreadyMapped = FileClientUtils.mappedContentsBy(hashOfContents);
		if (alreadyMapped != null)
			my(BlinkingLights.class).turnOn(LightType.WARNING, "Redundant download started", "File: " + fileOrFolder + " already mapped as: " + alreadyMapped + " hash: " + hashOfContents, 10000);
	}


	void waitTillFinished() throws IOException {
		_isFinished.waitTillOpen();
		if (_exception != null) throw _exception;
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


	private boolean isFinished() {
		return _isFinished.isOpen();
	}


	private void finishWith(IOException ioe) {
		_exception = ioe;
		_isFinished.open();
	}


	private void tryToReceiveFileBlock(FileContents contents) throws IOException {
		if (!contents.hashOfFile.equals(_hashOfContents)) return;

		my(Logger.class).log("File block received. File: {}, Block: ", contents.debugInfo, contents.blockNumber);

		if (contents instanceof FileContentsFirstBlock)
			receiveFirstBlock((FileContentsFirstBlock) contents);

		_blocksToWrite.add(contents);

		tryToWriteBlocksInSequence();
	}


	private void receiveFirstBlock(FileContentsFirstBlock contents) throws IOException {
		if (_fileSizeInBlocks != -1) return;
		_fileSizeInBlocks = calculateFileSizeInBlocks(contents.fileSize);
		_output = my(AtomicFileWriter.class).atomicOutputStreamFor(_fileOrFolder, _lastModified);
	}


	private int calculateFileSizeInBlocks(long fileSizeInBytes) {
		final int result = (int) fileSizeInBytes / Protocol.FILE_BLOCK_SIZE;
		return (fileSizeInBytes % Protocol.FILE_BLOCK_SIZE != 0) ? result + 1 : result; 
	}


	private void tryToWriteBlocksInSequence() throws IOException {
		boolean written = false;

		do {
			Iterator<FileContents> it = _blocksToWrite.iterator();
			while(it.hasNext()) {
				FileContents block = it.next();
				if (block.blockNumber != _nextBlockToWrite) continue;
				it.remove();
				writeBlock(block.bytes.copy());
				written = true;
			}
		} while (written); // In case blocks have arrived out of order

		if (written && !isFinished()) requestNextBlock();
	}


	private void writeBlock(byte[] bytes) throws IOException {
		_output.write(bytes);
		if (_nextBlockToWrite == _fileSizeInBlocks) finishDowload();
		++_nextBlockToWrite;
	}


	private void requestNextBlockIfNecessary() {
		if (_lastRequestTime == -1)
			requestNextBlock();

		if (my(Clock.class).time().currentValue() - _lastRequestTime >= REQUEST_PERIOD)
			requestNextBlock();
	}


	private void requestNextBlock() {
		my(TupleSpace.class).publish(new FileRequest(_hashOfContents, _nextBlockToWrite, _fileOrFolder.getAbsolutePath()));
		_lastRequestTime = my(Clock.class).time().currentValue();
	}


	private void receiveFolder(FolderContents contents) {
		Sneer1024 hash = my(Hasher.class).hash(contents);
		if (!hash.equals(_hashOfContents)) return;

//		finishDownloads(hash, contents);
		my(Logger.class).log("folder received", hash);
	}

//	private void recurseIfFolder(File fileOrFolder, long lastModified, Sneer1024 hashOfContents) throws IOException {
//		Object contents = FileClientUtils.mappedContentsBy(hashOfContents);
//		if (!(contents instanceof FolderContents)) return;
//		
//		if (!fileOrFolder.exists() && !fileOrFolder.mkdir()) throw new IOException("Unable to create folder: " + fileOrFolder);
//		for (FileOrFolder entry : ((FolderContents)contents).contents)
//			fetch(new File(fileOrFolder, entry.name), entry.lastModified, entry.hashOfContents);
//		
//		if (lastModified != -1)
//			fileOrFolder.setLastModified(lastModified);
//	}

	private void finishDowload() throws IOException {
		my(IO.class).streams().closeQuietly(_output);
		my(FileMap.class).put(_fileOrFolder);
		my(BlinkingLights.class).turnOn(LightType.GOOD_NEWS, _fileOrFolder.getName() + " downloaded!", _fileOrFolder.getAbsolutePath(), 10000);
		_isFinished.open();
	}

}

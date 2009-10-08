package sneer.bricks.hardwaresharing.files.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.hardwaresharing.files.writer.FileWriter;
import sneer.bricks.pulp.crypto.Sneer1024;


class Download {

	
	private final File _file;
	private final long _lastModified;
	
	private final Latch _finished = my(Latches.class).produce();
	private boolean _isFinished = false;
	private IOException _exception;

	
	Download(File file, long lastModified, Sneer1024 hashOfContents) {
		if (file == null) throw new IllegalArgumentException();
		_file = file;
		_lastModified = lastModified;
		
		Object alreadyMapped = FileClientUtils.mappedContentsBy(hashOfContents);
		if (alreadyMapped != null)
			finish(alreadyMapped);
	}

	
	boolean isFinished() {
		return _isFinished;
	}

	
	void waitTillFinished() throws IOException {
		_finished.waitTillOpen();
		if (_exception != null) throw _exception;
	}


	void finish(Object data) {
		try {
			tryToFinish(data);
		} catch (IOException e) {
			_exception = e;
		}
		_isFinished = true;
		_finished.open();
	}


	private void tryToFinish(Object data) throws IOException {
		if (data instanceof File)           finishWith((File)data);
		if (data instanceof FolderContents) finishWith((FolderContents)data);
		finishWith((byte[])data);
	}

	
	private void finishWith(File fileToCopy) throws IOException {
		byte[] contents = my(IO.class).files().readBytes(fileToCopy);
		finishWith(contents);
	}

	
	private void finishWith(byte[] contents) throws IOException {
		my(FileWriter.class).writeAtomicallyTo(_file, _lastModified, contents);
	}

	
	private void finishWith(FolderContents contents) {
		my(FileMap.class).putFolderContents(contents);
	}






	

}

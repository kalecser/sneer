package sneer.bricks.hardwaresharing.files.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardwaresharing.files.client.Download;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;

abstract class AbstractDownload implements Download {

	static final int REQUEST_INTERVAL = 15000;

	final Latch _isFinished = my(Latches.class).produce();
	private IOException _exception;

	public void waitTillFinished() throws IOException {
		_isFinished.waitTillOpen();
		if (_exception != null) throw _exception;
	}

	boolean isFinished() {
		return _isFinished.isOpen();
	}

	void finishWith(IOException ioe) {
		_exception = ioe;
		_isFinished.open();
	}

	void finishWith(File fileOrFolder) throws IOException {
		my(FileMap.class).put(fileOrFolder);
		my(BlinkingLights.class).turnOn(LightType.GOOD_NEWS, fileOrFolder.getName() + " downloaded!", fileOrFolder.getAbsolutePath(), 10000);
		_isFinished.open();
	}

}

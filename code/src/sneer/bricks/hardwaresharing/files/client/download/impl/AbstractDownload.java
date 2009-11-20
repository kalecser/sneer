package sneer.bricks.hardwaresharing.files.client.download.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardware.io.file.atomic.dotpart.DotParts;
import sneer.bricks.hardwaresharing.files.client.download.Download;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.brickness.Tuple;

abstract class AbstractDownload implements Download {

	static final int REQUEST_INTERVAL = 15000;

	final File _path;
	final long _lastModified;
	final Sneer1024 _hash;

	private File _actualPath;

	@SuppressWarnings("unused") private WeakContract _timerContract;

	final Latch _isFinished = my(Latches.class).produce();
	private IOException _exception;


	AbstractDownload(File path, long lastModified, Sneer1024 hashOfFile) {
		_path = dotPartFor(path);
		_lastModified = lastModified;
		_hash = hashOfFile;

		_actualPath = path;

		finishIfRedundant();
	}


	public void waitTillFinished() throws IOException {
		_isFinished.waitTillOpen();
		if (_exception != null) throw _exception;
	}


	private File dotPartFor(File path) {
		File dotPart = null;
		try {
			dotPart = my(DotParts.class).openDotPartFor(path);
		} catch (IOException e) {
			finishWith(e);
		}

		return dotPart;
	}


	private void publishRequestIfNecessary() {
		if (isFinished()) return;
		Tuple request = requestToPublishIfNecessary();
		if (request == null) return;
		publish(request);
	}


	void publish(Tuple request) {
		my(TupleSpace.class).publish(request);
	}


	abstract void copyContents(Object contents) throws IOException;
	abstract Tuple requestToPublishIfNecessary();


	boolean isFinished() {
		return _isFinished.isOpen();
	}


	void finishWith(IOException ioe) {
		_exception = ioe;
		_isFinished.open();
	}


	void finish() throws IOException {
		my(DotParts.class).closeDotPart(_path, _lastModified);

		my(FileMap.class).put(_actualPath);

		my(BlinkingLights.class).turnOn(LightType.GOOD_NEWS, _actualPath.getName() + " downloaded!", _actualPath.getAbsolutePath(), 10000);
		_isFinished.open();
	}


	private void finishIfRedundant() {
		Object alreadyMapped = mappedContentsBy(_hash);
		if (alreadyMapped == null) return;
		try {
			copyContents(alreadyMapped);
			finish();
		} catch (IOException ioe) {
			finishWith(ioe);
		}
	}


	abstract Object mappedContentsBy(Sneer1024 hashOfContents);


	void startSendingRequests() {
		_timerContract = my(Timer.class).wakeUpNowAndEvery(REQUEST_INTERVAL, new Runnable() { @Override public void run() {
			publishRequestIfNecessary();
		}});
	}

}

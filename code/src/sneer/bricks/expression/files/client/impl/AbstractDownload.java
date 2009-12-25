package sneer.bricks.expression.files.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.client.Download;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardware.io.files.atomic.dotpart.DotParts;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
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

	private final Runnable _toCallWhenFinished;


	AbstractDownload(File path, long lastModified, Sneer1024 hashOfFile, Runnable toCallWhenFinished) {
		_path = dotPartFor(path);
		_lastModified = lastModified;
		_hash = hashOfFile;

		_actualPath = path;

		_toCallWhenFinished = toCallWhenFinished;

		finishIfLocallyAvailable();
	}


	public void waitTillFinished() throws IOException {
		_isFinished.waitTillOpen();
		if (_exception != null) throw _exception;
	}


	@Override
	public void dispose() {
		finishWith(new IOException("Download disposed: " + _actualPath));
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
		finish();
	}


	void finishWithSuccess() throws IOException {
		my(DotParts.class).closeDotPart(_path, _lastModified);

		my(BlinkingLights.class).turnOn(LightType.GOOD_NEWS, _actualPath.getName() + " downloaded!", _actualPath.getAbsolutePath(), 10000);
		finish();
	}


	private void finish() {
		if (_toCallWhenFinished != null) _toCallWhenFinished.run();
		_isFinished.open();
	}


	private void finishIfLocallyAvailable() {
		Object alreadyMapped = mappedContentsBy(_hash);
		if (alreadyMapped == null) return;
		try {
			copyContents(alreadyMapped);
			finishWithSuccess();
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

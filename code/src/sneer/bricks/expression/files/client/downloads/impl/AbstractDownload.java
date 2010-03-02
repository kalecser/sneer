package sneer.bricks.expression.files.client.downloads.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.TimeoutException;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardware.io.files.atomic.dotpart.DotParts;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.events.pulsers.PulseSource;
import sneer.bricks.pulp.events.pulsers.Pulser;
import sneer.bricks.pulp.events.pulsers.Pulsers;
import sneer.bricks.pulp.tuples.Tuple;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Closure;

abstract class AbstractDownload implements Download {

	private static int TIMEOUT_LIMIT = 15 * 60 * 1000;

	static final int REQUEST_INTERVAL = 15 * 1000;

	File _path;
	final long _lastModified;
	final Sneer1024 _hash;

	private final File _actualPath;

	private long _startTime;

	private final Latch _isFinished = my(Latches.class).produce();
	private Pulser _finished = my(Pulsers.class).newInstance();

	private Exception _exception;

	private final Runnable _toCallWhenFinished;

	private WeakContract _timerContract;


	AbstractDownload(File path, long lastModified, Sneer1024 hashOfFile, Runnable toCallWhenFinished) {
		_path = dotPartFor(path);
		_lastModified = lastModified;
		_hash = hashOfFile;

		_actualPath = path;

		_toCallWhenFinished = toCallWhenFinished;

		finishIfLocallyAvailable();
	}


	void start() {
		if (isFinished()) return;

		subscribeToContents();
		_startTime = my(Clock.class).time().currentValue();
		startSendingRequests();
	}


	abstract void subscribeToContents();


	@Override
	public void waitTillFinished() throws IOException, TimeoutException {
		_isFinished.waitTillOpen();
		if (_exception != null)
			if (_exception instanceof IOException) throw (IOException) _exception;
			if (_exception instanceof TimeoutException) throw (TimeoutException) _exception;
	}


	@Override
	public void dispose() {
		finishWith(new IOException("Download disposed: " + _actualPath));
	}


	@Override
	public PulseSource finished() {
		return _finished.output();
	}


	@Override
	public boolean hasFinishedSuccessfully() {
		if (!isFinished()) return false;
		return _exception == null;
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

	
	abstract Tuple requestToPublishIfNecessary();
	

	void publish(Tuple request) {
		my(TupleSpace.class).acquire(request);
	}


	boolean isFinished() {
		return _isFinished.isOpen();
	}


	void finishWith(Exception e) {
		_exception = e;
		finish();
	}


	void finishWithSuccess() throws IOException {
		my(DotParts.class).closeDotPart(_path, _lastModified);
		updateFileMapWith(_path, _actualPath);

		my(BlinkingLights.class).turnOn(LightType.GOOD_NEWS, _actualPath.getName() + " downloaded!", _actualPath.getAbsolutePath(), 10000);
		finish();
	}


	abstract void updateFileMapWith(File tmpFile, File actualFile);


	void finish() {
		if (_toCallWhenFinished != null) _toCallWhenFinished.run();
		_finished.sendPulse();
		_isFinished.open();
		stopSendingRequests();
	}


	private void stopSendingRequests() {
		if (_timerContract == null) return;
		_timerContract.dispose();
	}


	void startSendingRequests() {
		_timerContract = my(Timer.class).wakeUpNowAndEvery(REQUEST_INTERVAL, new Closure() { @Override public void run() {
			checkForTimeOut();
			publishRequestIfNecessary();
		}});
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

	abstract void copyContents(Object contents) throws IOException;


	void checkForTimeOut() {
		if (my(Clock.class).time().currentValue() - _startTime >= TIMEOUT_LIMIT)
			finishWith(new TimeoutException());
	}

}

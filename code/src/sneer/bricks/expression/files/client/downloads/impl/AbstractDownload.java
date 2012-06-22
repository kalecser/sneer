package sneer.bricks.expression.files.client.downloads.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.TimeoutException;
import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.files.atomic.dotpart.DotParts;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.exceptionhandling.ExceptionHandler;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import basis.lang.Closure;
import basis.util.concurrent.Latch;

abstract class AbstractDownload implements Download {

	private static int ACTIVITY_TIMEOUT = 60 * 1000;
	private static int DURATION_TIMEOUT = 30 * 60 * 1000;
	private static boolean IGNORE_DURATION_TIMEOUT = true;

	static final int REQUEST_INTERVAL = 15 * 1000;
	
	private static final String DOT_PART = my(DotParts.class).dotPartExtention();
	

	protected File _path;
	protected final File _actualPath;
	final long _lastModified;
	final Hash _hash;


	protected final Seal _source;

	private long _startTime;
	private long _lastActivityTime;

	private final Register<Integer> _progress = my(Signals.class).newRegister(0);

	private final Latch _isFinished = new Latch();
	private Register<Boolean> _finished = my(Signals.class).newRegister(false);

	private Exception _exception;

	private final Collection<Runnable> _toCallWhenFinished = new ArrayList<Runnable>(1);

	private WeakContract _timerContract;
	protected final boolean _copyLocalFiles;


	AbstractDownload(File path, long lastModified, Hash hashOfFile, Seal source, boolean copyLocalFiles) {
		_actualPath = path;
		
		_path = dotPartFor(path);
		_lastModified = lastModified;
		_hash = hashOfFile;

		if (source == null) throw new IllegalArgumentException("Source seal cannot be null.");
		_source = source;

		_copyLocalFiles = copyLocalFiles;

		my(Logger.class).log("Downloading: {} Hash:", _actualPath, _hash);

		finishIfLocallyAvailable();
	}

	
	abstract protected void subscribeToContents();
	abstract protected Tuple requestToPublishIfNecessary();
	abstract protected boolean isWaitingForActivity();

	abstract protected Object mappedContentsBy(Hash hashOfContents) throws FileNotFoundException;
	abstract protected void finishWithLocalContents(Object contents) throws IOException, TimeoutException;
	
	abstract protected void updateFileMap();


	void start() {
		if (isFinished()) return;

		subscribeToContents();
		_startTime = my(Clock.class).time().currentValue();
		_lastActivityTime = _startTime;
		startSendingRequests();
	}


	@Override	public File file() {	return _actualPath; }
	@Override	public Hash hash() { return _hash; }
	@Override	public Seal source() { return _source; }
	@Override	public Signal<Integer> progress() { return _progress.output(); }


	protected void setProgress(float newValue) {
		_progress.setter().consume((int) (100 * newValue));
	}


	@Override
	public void waitTillFinished() throws IOException, TimeoutException {
		_isFinished.waitTillOpen();
		if (_exception == null) return;
		if (_exception instanceof IOException) throw (IOException) _exception;
		if (_exception instanceof TimeoutException) throw (TimeoutException) _exception;
		throw new IllegalStateException("Unexpected exception type: " + _exception.getClass(), _exception);
	}


	@Override
	public void dispose() {
		finishWith(new IOException("Download disposed: " + _actualPath));
	}


	@Override
	public Signal<Boolean> finished() {
		return _finished.output();
	}


	@Override
	public boolean hasFinishedSuccessfully() {
		if (!isFinished()) return false;
		return _exception == null;
	}
	
	@Override
	synchronized
	public void onFinished(Runnable action) {
		if (hasFinishedSuccessfully()) {
			action.run();
			return;
		}
		_toCallWhenFinished.add(action);
		
	}

	private File dotPartFor(File path) {
		try {
			return my(DotParts.class).openDotPartFor(path);
		} catch (IOException e) {
			finishWith(e);
			return null;
		}
	}


	private void publishRequestIfNecessary() {
		if (isFinished()) return;
		Tuple request = requestToPublishIfNecessary();
		if (request == null) return;
		publish(request);
	}


	protected void publish(Tuple request) {
		my(TupleSpace.class).add(request);
	}


	boolean isFinished() {
		return _isFinished.isOpen();
	}


	protected void finishWith(Exception e) {
		_exception = e;
		my(Logger.class).log("Download failed with: {} message: {}", _exception.getClass(), _exception.getMessage());
		finish();
	}


	protected void finishWithSuccess() throws IOException {
		my(DotParts.class).closeDotPart(_path, _lastModified);
		updateFileMap();
		finish();
	}


	private void finish() {
		stopSendingRequests();
		_isFinished.open();
		_finished.setter().consume(true);
		for (Runnable action : _toCallWhenFinished)
			my(ExceptionHandler.class).shield(action);
	}


	private void stopSendingRequests() {
		if (_timerContract == null) return;
		_timerContract.dispose();
	}


	void startSendingRequests() {
		_timerContract = my(Timer.class).wakeUpNowAndEvery(REQUEST_INTERVAL, new Closure() { @Override public void run() {
			checkForActivityTimeOut();
			checkForDurationTimeOut();
			publishRequestIfNecessary();
		}});
	}
	
	protected abstract String getMappedPath(Hash hash);

	private void finishIfLocallyAvailable() {
		String mappedPath = getMappedPath(_hash);
		if (mappedPath == null) return;
		if (!_copyLocalFiles) {
			updateFileMap();
			finish();
			return;
		}
		if (mappedPath.contains(DOT_PART)) return; // .part files might be renamed at any moment; Optimize Downloads that include identical files in different folders will download all of them redundantly. The problem is .part files can be renamed to their actual name at any moment. 
		
		try {
			finishWithLocalContents(mappedContentsBy(_hash));
		} catch (Exception e) {
			finishWith(new IllegalStateException(mappedPath, e));
		}
	}


	protected void recordActivity() {
		_lastActivityTime = my(Clock.class).time().currentValue();
	}


	private void checkForDurationTimeOut() {
		if (IGNORE_DURATION_TIMEOUT) return;
		Long currentTime = my(Clock.class).time().currentValue();
		if (currentTime - _startTime > DURATION_TIMEOUT) timeout("Duration");
	}

	
	private void checkForActivityTimeOut() {
		if(!isWaitingForActivity())
			return;
		Long currentTime = my(Clock.class).time().currentValue();
		if (currentTime - _lastActivityTime > ACTIVITY_TIMEOUT) timeout("Activity");
	}

	
	private void timeout(String timeoutCase) {
		finishWith(new TimeoutException(timeoutCase + " Timeout downloading " + _actualPath.getAbsolutePath()));
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_hash == null) ? 0 : _hash.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		AbstractDownload other = (AbstractDownload) obj;
		if (_hash == null) {
			if (other._hash != null)
				return false;
		} else if (!_hash.equals(other._hash))
			return false;
		return true;
	}


	@Override
	protected void finalize() throws Throwable {
		my(Logger.class).log("Download garbage collected: " + _path);
		this.dispose();
	}

}

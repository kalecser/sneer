package sneer.bricks.hardwaresharing.files.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
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
	
	@SuppressWarnings("unused") private WeakContract _timerContract;
	
	final Latch _isFinished = my(Latches.class).produce();
	private IOException _exception;

	
	AbstractDownload(File path, long lastModified, Sneer1024 hashOfFile) {
		_path = path;
		_lastModified = lastModified;
		_hash = hashOfFile;
		
		finishIfRedundant();
	}

	
	public void waitTillFinished() throws IOException {
		_isFinished.waitTillOpen();
		if (_exception != null) throw _exception;
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
	

	protected abstract void copyContents(Object contents) throws IOException;
	protected abstract Tuple requestToPublishIfNecessary();


	boolean isFinished() {
		return _isFinished.isOpen();
	}

	
	void finishWith(IOException ioe) {
		_exception = ioe;
		_isFinished.open();
	}

	
	void finish() throws IOException {
		my(FileMap.class).put(_path);
		
		//Implement: .part logic.
		
	    if (_lastModified != -1)
		      _path.setLastModified(_lastModified);
		
		my(BlinkingLights.class).turnOn(LightType.GOOD_NEWS, _path.getName() + " downloaded!", _path.getAbsolutePath(), 10000);
		_isFinished.open();
	}

	
	private void finishIfRedundant() {
		Object alreadyMapped = FileClientUtils.mappedContentsBy(_hash);
		if (alreadyMapped == null) return;
		try {
			copyContents(alreadyMapped);
			finish();
		} catch (IOException ioe) {
			finishWith(ioe);
		}
	}


	protected void startSendingRequests() {
		_timerContract = my(Timer.class).wakeUpNowAndEvery(REQUEST_INTERVAL, new Runnable() { @Override public void run() {
			publishRequestIfNecessary();
		}});
	}

}

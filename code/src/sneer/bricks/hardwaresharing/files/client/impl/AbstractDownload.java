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

	
	AbstractDownload(File file, long lastModified, Sneer1024 hashOfFile) {
		_path = file;
		_lastModified = lastModified;
		_hash = hashOfFile;
		
		checkRedundantDownload();
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
	
		
	abstract Tuple requestToPublishIfNecessary();


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

	
	private void checkRedundantDownload() {
		Object alreadyMapped = FileClientUtils.mappedContentsBy(_hash);
		if (alreadyMapped != null)
			my(BlinkingLights.class).turnOn(LightType.WARNING, "Redundant download started", "Path: " + _path + " already mapped as: " + alreadyMapped + " hash: " + _hash, 10000);
	}


	protected void startSendingRequests() {
		_timerContract = my(Timer.class).wakeUpNowAndEvery(REQUEST_INTERVAL, new Runnable() { @Override public void run() {
			publishRequestIfNecessary();
		}});
	}

}

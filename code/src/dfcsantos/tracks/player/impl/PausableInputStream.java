package dfcsantos.tracks.player.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;

class PausableInputStream extends BufferedInputStream {

	private final Object _stateMonitor = new Object();
	private Register<Boolean> _isPaused = my(Signals.class).newRegister(false);
	private boolean _isClosed;

	PausableInputStream(InputStream inputStream) {
		super(inputStream);
	}

	void pauseResume() {
		synchronized (_stateMonitor) {
			if (_isClosed) return;
			_isPaused.setter().consume(!_isPaused.output().currentValue());
			_stateMonitor.notify();
		}
	}

	@Override
	public void close() {
		try {
			super.close();
		} catch (IOException e) {}

		synchronized (_stateMonitor) {
			_isClosed = true;
			_isPaused.setter().consume(false);
			_stateMonitor.notify();
		}
	}

	private void pauseIfNecessary() {
		synchronized (_stateMonitor) {
			while (_isPaused.output().currentValue())
				my(Threads.class).waitWithoutInterruptions(_stateMonitor);
		}
	}

	@Override
	public synchronized int read() throws IOException {
		pauseIfNecessary();
		return super.read(); 
	}

	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		pauseIfNecessary();
		return super.read(b, off, len);
	}

	@Override
	public int read(byte[] b) throws IOException {
		pauseIfNecessary();
		return super.read(b);
	}
	
	public Signal<Boolean> isPaused(){
		return _isPaused.output(); 
	}

}

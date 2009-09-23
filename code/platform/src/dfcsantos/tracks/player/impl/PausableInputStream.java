package dfcsantos.tracks.player.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import sneer.bricks.hardware.cpu.threads.Threads;

class PausableInputStream extends BufferedInputStream {

	private final Object _stateMonitor = new Object();
	private boolean _isPaused;
	private boolean _isStopped;

	PausableInputStream(InputStream inputStream) {
		super(inputStream);
	}

	void pauseResume() {
		synchronized (_stateMonitor) {
			_isPaused = !_isPaused;
			_stateMonitor.notify();
		}
	}

	private boolean shouldStop() {
		synchronized (_stateMonitor) {
			while (true) {
				if (_isStopped)
					return true;
				if (!_isPaused)
					return false;
				my(Threads.class).waitWithoutInterruptions(_stateMonitor);
			}
		}
	}

	void stop() {
		synchronized (_stateMonitor) {
			_isStopped = true;
			_stateMonitor.notify();
		}
	}

	@Override
	public synchronized int read() throws IOException {
		return shouldStop() ? -1 : super.read(); 
	}

	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		return shouldStop() ? -1 : super.read(b, off, len);
	}

	@Override
	public int read(byte[] b) throws IOException {
		return shouldStop() ? -1 : super.read(b);
	}

}

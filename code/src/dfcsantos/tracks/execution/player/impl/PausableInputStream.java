package dfcsantos.tracks.execution.player.impl;

import static basis.environments.Environments.my;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import basis.lang.Consumer;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.reactive.Signal;

class PausableInputStream extends BufferedInputStream {

	private final Object _stateMonitor = new Object();
	private boolean _isPaused;
	private boolean _isClosed;

	@SuppressWarnings("unused") private final Object _refToAvoidGc;

	PausableInputStream(InputStream inputStream, Signal<Boolean> isPlaying) {
		super(inputStream);

		_refToAvoidGc = isPlaying.addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean playing) {
			setPaused(!playing);
		}});
	}

	private void setPaused(boolean paused) {
		synchronized (_stateMonitor) {
			if (_isClosed) return;
			_isPaused = paused;
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
			_isPaused = false;
			_stateMonitor.notify();
		}
	}

	private void pauseIfNecessary() {
		synchronized (_stateMonitor) {
			while (_isPaused)
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

}

package dfcsantos.tracks.player.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.player.Player;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Signal;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.player.TrackContract;

class TrackContractImpl implements TrackContract {

	private PausableInputStream _trackStream;
	private Player _player;

	private volatile boolean _wasDisposed;

	TrackContractImpl(final Track track, final Runnable toCallWhenFinished) {
		my(Threads.class).startDaemon("Track Player", new Runnable() { @Override public void run() {
			try {
				_trackStream = new PausableInputStream(new FileInputStream(track.file()));
				_player = new Player(_trackStream);
				_player.play();
				
			} catch (FileNotFoundException e) {
				my(BlinkingLights.class).turnOn(LightType.WARN, "Unable to find file " + track.file() , "File might have been deleted manually.", 15000);
				
			} catch (Throwable t) {
				if (!_wasDisposed)
					my(BlinkingLights.class).turnOn(LightType.WARN, "Error reading track", "Error reading track", t, 30000);
				
			} finally {
				if (_wasDisposed) return;
				dispose();
				toCallWhenFinished.run();
			}
		}});
	}

	@Override
	public void pauseResume() {
		_trackStream.pauseResume();
	}

	@Override
	public void dispose() {
		_wasDisposed = true;
		if (_player != null) _player.close();
	}

	@Override
	public int trackElapsedTime() {
		return _player == null ? 0 : _player.getPosition();
	}

	@Override
	public Signal<Boolean> isPaused() {
		return _trackStream.isPaused();
	}

}

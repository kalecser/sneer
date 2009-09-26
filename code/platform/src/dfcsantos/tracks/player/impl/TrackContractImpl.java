package dfcsantos.tracks.player.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import dfcsantos.tracks.player.TrackContract;

class TrackContractImpl implements TrackContract {

	private final PausableInputStream _trackStream;
	private Player _player;

	private volatile boolean _wasDisposed;

	TrackContractImpl(InputStream stream, final Runnable toCallWhenFinished) {
		_trackStream = new PausableInputStream(stream);

		my(Threads.class).startDaemon("Track Player", new Runnable() { @Override public void run() {
			try {
				_player = new Player(_trackStream);
				_player.play();
			} catch (JavaLayerException e) {
				if (!_wasDisposed)
					my(BlinkingLights.class).turnOn(LightType.WARN, "Error reading track", "Error reading track", e, 30000);
			}

			if (!_wasDisposed) toCallWhenFinished.run();
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

}

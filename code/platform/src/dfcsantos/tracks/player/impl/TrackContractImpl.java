package dfcsantos.tracks.player.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import sneer.bricks.hardware.cpu.threads.Threads;
import dfcsantos.tracks.player.TrackContract;

class TrackContractImpl implements TrackContract {

	private final PausableInputStream _trackStream;

	TrackContractImpl(InputStream stream, final Runnable toCallWhenFinished) {
		_trackStream = new PausableInputStream(stream);
		final Player player;

		try {
			player = new Player(_trackStream);

		} catch (JavaLayerException e1) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e1); // Fix Handle this exception.
		}

		my(Threads.class).startDaemon("Track Player", new Runnable() { @Override public void run() {
			try {
				player.play();
			} catch (JavaLayerException e) {
				throw new sneer.foundation.lang.exceptions.NotImplementedYet(e);
			}

			toCallWhenFinished.run();
		}});
	}

	@Override
	public void pauseResume() {
	 _trackStream.pauseResume();
	}

	@Override
	public void dispose() {
		_trackStream.stop();
	}

}

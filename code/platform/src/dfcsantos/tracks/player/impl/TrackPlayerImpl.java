package dfcsantos.tracks.player.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.foundation.lang.ByRef;
import dfcsantos.tracks.player.TrackContract;
import dfcsantos.tracks.player.TrackPlayer;


class TrackPlayerImpl implements TrackPlayer {

	private Player _player;
	
	@Override
	public TrackContract startPlaying(InputStream stream, final Runnable toCallWhenFinished) {
		BufferedInputStream bis = new BufferedInputStream(stream);
		try {
			_player = new Player(bis);
		} catch (JavaLayerException e1) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e1); // Fix Handle this exception.
		}
		
		final ByRef<Thread> playerThread = ByRef.newInstance();
		
		my(Threads.class).startDaemon("Track Player", new Runnable() { @Override public void run() {
			playerThread.value = Thread.currentThread();
			try {
				_player.play();
				toCallWhenFinished.run();
			} catch (JavaLayerException e) {
				throw new sneer.foundation.lang.exceptions.NotImplementedYet(e);
			}
		}});
		
		return new TrackContractImpl(playerThread.value);
	}

}

package dfcsantos.songplayer.impl;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import dfcsantos.songplayer.SongContract;
import dfcsantos.songplayer.SongPlayer;

class SongPlayerImpl implements SongPlayer {

	private Player _player;
	
	@Override
	public SongContract startPlaying(InputStream stream, Runnable toCallWhenFinished) {
		BufferedInputStream bis = new BufferedInputStream(stream);
		try {
			_player = new Player(bis);
		} catch (JavaLayerException e1) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e1); // Fix Handle this exception.
		}
		Thread playerThread = new Thread() {  @Override public void run() {
			try {
				_player.play();
			} catch (JavaLayerException e) {
				e.printStackTrace();
			}
		}};
		SongContractImpl mp3PlayingContract = new SongContractImpl(playerThread);
		return mp3PlayingContract;
	}
}

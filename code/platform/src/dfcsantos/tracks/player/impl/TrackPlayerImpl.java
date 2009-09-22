package dfcsantos.tracks.player.impl;

import java.io.InputStream;

import dfcsantos.tracks.player.TrackContract;
import dfcsantos.tracks.player.TrackPlayer;

class TrackPlayerImpl implements TrackPlayer {

	@Override
	public TrackContract startPlaying(InputStream stream, final Runnable toCallWhenFinished) {
		return new TrackContractImpl(stream, toCallWhenFinished);
	}

}

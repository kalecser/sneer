package dfcsantos.tracks.player.impl;

import dfcsantos.tracks.Track;
import dfcsantos.tracks.player.TrackContract;
import dfcsantos.tracks.player.TrackPlayer;

class TrackPlayerImpl implements TrackPlayer {

	@Override
	public TrackContract startPlaying(Track track, final Runnable toCallWhenFinished) {
		return new TrackContractImpl(track, toCallWhenFinished);
	}

}

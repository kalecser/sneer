package dfcsantos.tracks.player.impl;

import sneer.bricks.pulp.reactive.Signal;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.player.TrackContract;
import dfcsantos.tracks.player.TrackPlayer;

class TrackPlayerImpl implements TrackPlayer {

	@Override
	public TrackContract startPlaying(Track track, Signal<Boolean> isPlaying, final Runnable toCallWhenFinished) {
		return new TrackContractImpl(track, isPlaying, toCallWhenFinished);
	}

}

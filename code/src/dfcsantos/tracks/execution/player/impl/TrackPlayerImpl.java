package dfcsantos.tracks.execution.player.impl;

import sneer.bricks.pulp.reactive.Signal;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.execution.player.TrackContract;
import dfcsantos.tracks.execution.player.TrackPlayer;

class TrackPlayerImpl implements TrackPlayer {

	@Override
	public TrackContract startPlaying(Track track, Signal<Boolean> isPlaying, Signal<Integer> volumePercent, final Runnable toCallWhenFinished) {
		return new TrackContractImpl(track, isPlaying, volumePercent, toCallWhenFinished);
	}

}

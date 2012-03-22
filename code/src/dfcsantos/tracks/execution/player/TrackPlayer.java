package dfcsantos.tracks.execution.player;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.Signal;
import dfcsantos.tracks.Track;

@Brick
public interface TrackPlayer {

	TrackContract startPlaying(Track track, Signal<Boolean> isPlaying, Signal<Integer> volumePercent, Runnable toCallWhenFinished);

}
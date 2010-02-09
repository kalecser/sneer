package dfcsantos.tracks.execution.player;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;
import dfcsantos.tracks.Track;

@Brick
public interface TrackPlayer {

	TrackContract startPlaying(Track track, Signal<Boolean> isPlaying, Runnable toCallWhenFinished);

}
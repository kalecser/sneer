package dfcsantos.tracks.player;

import sneer.foundation.brickness.Brick;
import dfcsantos.tracks.Track;

@Brick
public interface TrackPlayer {

	TrackContract startPlaying(Track track, Runnable toCallWhenFinished);

}
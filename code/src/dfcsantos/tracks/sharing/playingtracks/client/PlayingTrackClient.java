package dfcsantos.tracks.sharing.playingtracks.client;

import dfcsantos.tracks.sharing.playingtracks.protocol.PlayingTrack;
import sneer.bricks.software.bricks.snappstarter.Snapp;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;

@Snapp
@Brick
public interface PlayingTrackClient extends Consumer<PlayingTrack> {}

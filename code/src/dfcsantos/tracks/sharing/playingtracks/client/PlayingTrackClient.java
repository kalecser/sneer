package dfcsantos.tracks.sharing.playingtracks.client;

import dfcsantos.tracks.sharing.playingtracks.protocol.PlayingTrack;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;

@Brick (isSnapp = true)
public interface PlayingTrackClient extends Consumer<PlayingTrack> {}

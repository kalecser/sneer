package dfcsantos.tracks.monitor.rejected;

import java.util.List;

import sneer.foundation.brickness.Brick;
import dfcsantos.tracks.Track;

@Brick
public interface RejectedTracksKeeper {

	List<Track> rejectedTracks();

}

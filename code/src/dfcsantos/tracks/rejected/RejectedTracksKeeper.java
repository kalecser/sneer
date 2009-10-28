package dfcsantos.tracks.rejected;

import java.util.List;

import sneer.foundation.brickness.Brick;
import dfcsantos.tracks.Track;

@Brick
public interface RejectedTracksKeeper {

	void reject(Track victim);

	boolean isRejected(Track suspect);

	List<Track> rejected();

}

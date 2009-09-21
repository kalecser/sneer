package dfcsantos.tracks.announcer;

import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.brickness.Tuple;

public class TrackAnnouncement extends Tuple {

	public final String path;
	public final Sneer1024 hash;

	public TrackAnnouncement(String path_, Sneer1024 hash_) {
		path = path_;
		hash = hash_;
	}

}

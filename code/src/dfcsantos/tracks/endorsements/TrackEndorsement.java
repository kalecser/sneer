package dfcsantos.tracks.endorsements;

import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.brickness.Tuple;

public class TrackEndorsement extends Tuple {

	public final String path;
	public final long lastModified;
	public final Sneer1024 hash;

	public TrackEndorsement(String path_, long lastModified_, Sneer1024 hash_) {
		path = path_;
		lastModified = lastModified_;
		hash = hash_;
	}

}

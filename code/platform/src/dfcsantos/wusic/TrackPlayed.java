package dfcsantos.wusic;

import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.brickness.Tuple;

public class TrackPlayed extends Tuple {

	public final String path;
	public final Sneer1024 hash;

	public TrackPlayed(String path_, Sneer1024 hash_) {
		path = path_;
		hash = hash_;
	}

}

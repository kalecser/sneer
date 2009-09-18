package dfcsantos.wusic;

import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.brickness.Tuple;

public class SongPlayed extends Tuple {

	public final String path;
	public final Sneer1024 hash;

	public SongPlayed(String path_, Sneer1024 hash_) {
		path = path_;
		hash = hash_;
	}

}

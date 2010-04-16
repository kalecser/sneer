package dfcsantos.tracks.endorsements.protocol;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.cpu.crypto.Hash;

public class TrackEndorsement extends Tuple {

	public final String path;
	public final long lastModified;
	public final Hash hash;

	public TrackEndorsement(String path_, long lastModified_, Hash hash_) {
		path = path_;
		lastModified = lastModified_;
		hash = hash_;
	}

}

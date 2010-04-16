package sneer.bricks.softwaresharing.publisher;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.cpu.crypto.Hash;

/** Conveys the hash of the entire source folder of a given publisher at a certain point in time. */
public class SrcFolderHash extends Tuple {

	public final Hash value;

	public SrcFolderHash(Hash srcFolderHash_) {
		value = srcFolderHash_;
	}

}

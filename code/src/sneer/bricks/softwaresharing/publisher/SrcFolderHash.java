package sneer.bricks.softwaresharing.publisher;

import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.foundation.brickness.Tuple;

/** Conveys the hash of the entire source folder of a given publisher at a certain point in time. */
public class SrcFolderHash extends Tuple {

	public final Sneer1024 value;

	public SrcFolderHash(Sneer1024 srcFolderHash_) {
		value = srcFolderHash_;
	}

}

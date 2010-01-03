package sneer.bricks.pulp.keymanager;

import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.foundation.brickness.Immutable;

public class Seal extends Immutable {

	public final ImmutableByteArray bytes;

	public Seal(ImmutableByteArray bytes_) {
		bytes = bytes_;
	}

}

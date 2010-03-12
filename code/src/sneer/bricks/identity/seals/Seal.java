package sneer.bricks.identity.seals;

import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.foundation.lang.Immutable;

public class Seal extends Immutable {

	public final ImmutableByteArray bytes;

	public Seal(ImmutableByteArray bytes_) {
		bytes = bytes_;
	}

}

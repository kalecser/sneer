package sneer.bricks.hardware.cpu.crypto;

import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.foundation.lang.Immutable;

public class Hash extends Immutable {

	public final ImmutableByteArray bytes;

	public Hash(ImmutableByteArray bytes_) {
		bytes = bytes_;
	}

	
}


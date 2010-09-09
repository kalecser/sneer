package sneer.bricks.hardware.cpu.crypto;

import sneer.foundation.lang.Immutable;
import sneer.foundation.lang.arrays.ImmutableByteArray;

public class Hash extends Immutable {

	public final ImmutableByteArray bytes;

	public Hash(ImmutableByteArray bytes_) {
		bytes = bytes_;
	}

	public Hash(byte[] bytes_) {
		this(new ImmutableByteArray(bytes_));
	}

	
}


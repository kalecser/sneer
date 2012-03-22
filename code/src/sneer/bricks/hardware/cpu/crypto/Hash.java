package sneer.bricks.hardware.cpu.crypto;

import basis.lang.Immutable;
import basis.lang.arrays.ImmutableByteArray;

public class Hash extends Immutable {

	public final ImmutableByteArray bytes;

	public Hash(ImmutableByteArray bytes_) {
		bytes = bytes_;
	}

	public Hash(byte[] bytes_) {
		this(new ImmutableByteArray(bytes_));
	}

	
}


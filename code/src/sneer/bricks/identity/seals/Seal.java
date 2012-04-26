package sneer.bricks.identity.seals;

import basis.lang.arrays.ImmutableByteArray;
import sneer.bricks.hardware.cpu.crypto.Hash;

public class Seal extends Hash {

	public static int SIZE_IN_BYTES = 512 / 8; //SHA512

	public Seal(ImmutableByteArray bytes_) {
		super(bytes_);
	}

	public Seal(byte[] bytes_) {
		super(bytes_);
	}
	
}

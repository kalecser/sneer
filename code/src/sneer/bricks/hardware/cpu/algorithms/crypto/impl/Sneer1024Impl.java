package sneer.bricks.hardware.cpu.algorithms.crypto.impl;

import java.util.Arrays;

import sneer.bricks.hardware.cpu.algorithms.crypto.Crypto;
import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import static sneer.foundation.environments.Environments.my;

class Sneer1024Impl implements Sneer1024 {

	private static final long serialVersionUID = 1L;

	private byte[] _bytes;
	
	public Sneer1024Impl(byte[] bytes) {
		if (bytes.length != 128) throw new IllegalArgumentException();		
		_bytes = bytes;
	}

	@Override
	public byte[] bytes() {
		return _bytes.clone();
	}

	@Override
	public String toHexa() {
		return my(Crypto.class).toHexa(_bytes);
	}

	@Override
	public String toString() {
		return "" + _bytes[0] + _bytes[1] + _bytes[2]; 
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(_bytes); //Optimize Use only the first 4 bytes. They already are mashed up enough. :)
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null) return false;
		if (!(other instanceof Sneer1024)) return false;
		return Arrays.equals(_bytes, ((Sneer1024)other).bytes());
	}
}

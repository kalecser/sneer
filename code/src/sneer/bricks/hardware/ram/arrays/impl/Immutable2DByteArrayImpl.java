package sneer.bricks.hardware.ram.arrays.impl;

import java.util.Arrays;

import sneer.bricks.hardware.ram.arrays.Immutable2DByteArray;

class Immutable2DByteArrayImpl implements Immutable2DByteArray {

	private final byte[][] _payload;

	Immutable2DByteArrayImpl(byte[][] bufferToCopy) {
		_payload = copy(bufferToCopy);
	}

	@Override
	public byte[][] copy() {
		return copy(_payload);
	}

	@Override
	public byte[] get(int index) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	private static byte[][] copy(byte[][] original) {
		byte[][] result = new byte[original.length][0];
		
		for (int i = 0; i < original.length; i++)
			result[i] = Arrays.copyOf(original[i], original[i].length);
		
		return result;
	}

}

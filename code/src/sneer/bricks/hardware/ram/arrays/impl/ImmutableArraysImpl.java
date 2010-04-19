package sneer.bricks.hardware.ram.arrays.impl;

import java.util.Collection;

import sneer.bricks.hardware.ram.arrays.Immutable2DByteArray;
import sneer.bricks.hardware.ram.arrays.ImmutableArray;
import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;

class ImmutableArraysImpl implements ImmutableArrays {

	@Override
	public <T> ImmutableArray<T> newImmutableArray(T[] elements) {
		return new ImmutableArrayImpl<T>(elements);
	}

	@Override
	public <T> ImmutableArray<T> newImmutableArray(Collection<T> elements) {
		return new ImmutableArrayImpl<T>(elements);
	}

	@Override
	public ImmutableByteArray newImmutableByteArray(byte[] elements) {
		return new ImmutableByteArrayImpl(elements);
	}

	@Override
	public ImmutableByteArray newImmutableByteArray(byte[] elements, int size) {
		return new ImmutableByteArrayImpl(elements, size);
	}

	@Override
	public Immutable2DByteArray newImmutable2DByteArray(byte[][] elements) {
		return new Immutable2DByteArrayImpl(elements);
	}

}

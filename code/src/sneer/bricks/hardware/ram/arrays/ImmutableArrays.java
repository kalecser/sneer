package sneer.bricks.hardware.ram.arrays;

import java.util.Collection;

import sneer.foundation.brickness.Brick;

@Brick
public interface ImmutableArrays {

	<T> ImmutableArray<T> newImmutableArray(T[] elements);
	<T> ImmutableArray<T> newImmutableArray(Collection<T> elements);

	ImmutableByteArray newImmutableByteArray(byte[] elements);
	ImmutableByteArray newImmutableByteArray(byte[] elements, int size);

	Immutable2DByteArray newImmutable2DByteArray(byte[][] elements);

}

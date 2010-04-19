package sneer.bricks.hardware.ram.arrays;

public interface ImmutableByteArray {

	abstract byte get(int index);

	/** Returns the number of bytes copied to dest (length of this array)*/
	abstract int copyTo(byte[] dest);

	abstract byte[] copy();

	abstract String toString();

	abstract int hashCode();

	abstract boolean equals(Object obj);

}

package sneer.bricks.hardware.ram.arrays;

public interface Immutable2DByteArray {

	abstract byte[][] copy();

	abstract byte[] get(int index);

}

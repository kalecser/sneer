package sneer.bricks.hardware.ram.deepcopy;

import java.io.IOException;

import sneer.foundation.brickness.Brick;

@Brick
public interface DeepCopier {

	/** Produce a deep copy of the given object. Serializes the entire object to a byte array in memory. Recommended for relatively small objects, such as individual transactions. */
	<T> T deepCopy(T original);

	/**
	 * Produce a deep copy of the given object. Serializes the object through a pipe between two threads. Recommended for
	 * very large objects, such as an entire prevalent system. The current thread is used for serializing the original
	 * object in order to respect any synchronization the caller may have around it, and a new thread is used for
	 * deserializing the copy.
	 * @throws IOException 
	 */
	Object deepCopyThroughPipe(Object original);
}
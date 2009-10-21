package sneer.bricks.pulp.serialization;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.hardwaresharing.files.protocol.FileContents;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class SerializerTest extends BrickTest {

	private final Serializer _subject = my(Serializer.class);
	
	@Test
	public void largeArray() throws Exception {
		int size = 4000001;
		byte[] bytes = new byte[size];
		bytes[0] = 42;
		bytes[bytes.length - 1] = 43;
		FileContents contents = new FileContents(my(ImmutableArrays.class).newImmutableByteArray(bytes));
		
		byte[] serialized = _subject.serialize(contents);
		FileContents copy = (FileContents) _subject.deserialize(serialized, FileContents.class.getClassLoader());

		byte[] bytesCopy = copy.bytes.copy();
		assertEquals(42, bytesCopy[0]);
		assertEquals(43, bytesCopy[bytes.length - 1]);
	}
	
}

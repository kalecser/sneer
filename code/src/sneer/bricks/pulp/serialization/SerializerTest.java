package sneer.bricks.pulp.serialization;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.hardwaresharing.files.protocol.OldFileContents;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.Seal;

public class SerializerTest extends BrickTest {

	private final Serializer _subject = my(Serializer.class);
	
	@Test
	public void largeArray() throws Exception {
		Seal addressee = my(Seals.class).ownSeal();
		
		int size = 4000001;
		byte[] bytes = new byte[size];
		bytes[0] = 42;
		bytes[bytes.length - 1] = 43;
		ImmutableByteArray immutableBytes = my(ImmutableArrays.class).newImmutableByteArray(bytes);

		OldFileContents contents = new OldFileContents(addressee, immutableBytes, "foo debug info");
		
		byte[] serialized = _subject.serialize(contents);
		OldFileContents copy = (OldFileContents) _subject.deserialize(serialized, OldFileContents.class.getClassLoader());

		assertEquals(addressee, copy.addressee);
		byte[] bytesCopy = copy.bytes.copy();
		assertEquals(42, bytesCopy[0]);
		assertEquals(43, bytesCopy[bytes.length - 1]);
	}
	
}

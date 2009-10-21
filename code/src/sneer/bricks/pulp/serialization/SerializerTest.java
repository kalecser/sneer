package sneer.bricks.pulp.serialization;

import static sneer.foundation.environments.Environments.my;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class SerializerTest extends BrickTest {

	private final Serializer _subject = my(Serializer.class);
	
	@Ignore
	@Test
	public void largeArray() {
		int size = 1000000;
		ImmutableByteArray array = my(ImmutableArrays.class).newImmutableByteArray(new byte[size]);
		_subject.serialize(array);
	}
	
}

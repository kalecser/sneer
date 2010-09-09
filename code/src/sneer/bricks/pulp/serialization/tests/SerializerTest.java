package sneer.bricks.pulp.serialization.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.pulp.serialization.Serializer;
import sneer.bricks.pulp.serialization.tests.fixtures.brickwithlib.BrickToBeSerialized;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class SerializerTest extends BrickTestBase {

	private final Serializer _subject = my(Serializer.class);
	
	@Test
	public void classloaderHandling() throws Exception {
		BrickToBeSerialized object = my(BrickToBeSerialized.class);
		BrickToBeSerialized clone = (BrickToBeSerialized)_subject.deserialize(_subject.serialize(object));
		
		assertSame(object.getClass().getClassLoader(), clone.getClass().getClassLoader());
		assertSame(object.libClassLoader(), clone.libClassLoader());
	}
	
}

package sneer.bricks.hardwaresharing.files.map.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.hardwaresharing.files.hasher.Hasher;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.pulp.crypto.Crypto;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;

public class FileMapTest extends BrickTest {

	private final FileMap _subject = my(FileMap.class);

	@Bind private final Hasher _hasher = mock(Hasher.class);
	
	
	@Test
	public void keepFileByHash() throws IOException {
		final File file = new File("foo");
		final Sneer1024 hash = my(Crypto.class).digest(new byte[]{42});
		
		checking(new Expectations() {{
			oneOf(_hasher).hash(file); will(returnValue(hash));
		}});
		
		assertEquals(hash, _subject.put(file));
	}

}

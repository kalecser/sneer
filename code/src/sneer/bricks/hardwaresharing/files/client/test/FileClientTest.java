package sneer.bricks.hardwaresharing.files.client.test;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class FileClientTest extends BrickTest {

	private final FileMap _fileMap = mock(FileMap.class);

	@Ignore
	@Test
	public void downloadFileOnlyOnce() throws IOException {
		final Sneer1024 hash = _fileMap.put(myClassFile());

		// TODO: Create expectation to check that the file has not been requested twice 
		my(FileMap.class).put(myClassFile());
		my(FileClient.class).fetch(myClassFile(), hash);
		my(FileClient.class).fetch(myClassFile(), hash);
	}

	private File myClassFile() {
		return my(ClassUtils.class).classFile(getClass());
	}

}

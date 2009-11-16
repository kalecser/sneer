package sneer.bricks.hardwaresharing.files.client.test;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.hardwaresharing.files.protocol.FileRequest;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.lang.Consumer;

public class FileClientTest extends BrickTest {

	@Test (timeout = 2000)
	public void fileAlreadyMappedIsNotDownloaded() throws IOException {
		final Sneer1024 hash = my(FileMap.class).put(myClassFile());
		final File tmpFile = createTmpFile("tmpFile");
		my(TupleSpace.class).addSubscription(FileRequest.class, new Consumer<FileRequest>() { @Override public void consume(FileRequest request) {
			throw new IllegalStateException();
		}});
		my(FileClient.class).fetchFile(tmpFile, hash);
		my(IO.class).files().assertSameContents(tmpFile, myClassFile());
		my(TupleSpace.class).waitForAllDispatchingToFinish();
	}

	private File myClassFile() {
		return my(ClassUtils.class).classFile(getClass());
	}

}

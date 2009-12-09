package sneer.bricks.hardwaresharing.files.client.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.hardwaresharing.files.protocol.FileRequest;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.lang.Consumer;

public class FileClientTest extends BrickTest {

	@Test (timeout = 3000)
	public void fileAlreadyMappedIsNotDownloaded() throws IOException {
		final Sneer1024 hash = my(FileMap.class).put(myClassFile());
		my(TupleSpace.class).addSubscription(FileRequest.class, new Consumer<FileRequest>() { @Override public void consume(FileRequest request) {
			throw new IllegalStateException();
		}});

		final File tmpFile = newTmpFile();
		my(FileClient.class).startFileDownload(tmpFile, hash);

		my(TupleSpace.class).waitForAllDispatchingToFinish();
		my(IO.class).files().assertSameContents(tmpFile, myClassFile());
	}

	private File myClassFile() {
		return my(ClassUtils.class).classFile(getClass());
	}

}

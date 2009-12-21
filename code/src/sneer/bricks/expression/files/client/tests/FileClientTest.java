package sneer.bricks.expression.files.client.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FileRequest;
import sneer.bricks.hardware.cpu.algorithms.crypto.Crypto;
import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.lang.Consumer;

public class FileClientTest extends BrickTest {

	@Test (timeout = 3000)
	public void fileAlreadyMappedIsNotDownloaded() throws IOException {
		File myClassFile = my(ClassUtils.class).classFile(getClass());
		Sneer1024 hash = my(Crypto.class).digest(myClassFile); 

		my(FileMap.class).putFile(myClassFile, hash);

		my(TupleSpace.class).addSubscription(FileRequest.class, new Consumer<FileRequest>() { @Override public void consume(FileRequest request) {
			throw new IllegalStateException();
		}});

		File tmpFile = newTmpFile();
		my(FileClient.class).startFileDownload(tmpFile, hash);

		my(TupleSpace.class).waitForAllDispatchingToFinish();
		my(IO.class).files().assertSameContents(tmpFile, my(ClassUtils.class).classFile(getClass()));
	}

}

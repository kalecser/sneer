package sneer.bricks.expression.files.client.tests;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FileRequest;
import sneer.bricks.expression.tuples.dispatcher.TupleDispatcher;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.network.computers.channels.Channels;
import sneer.bricks.software.code.classutils.ClassUtils;
import basis.lang.Consumer;

public class FileClientTest extends BrickTestWithTuples {

	private final FileClient _subject = my(FileClient.class);

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;

	@Test (timeout = 3000)
	public void fileAlreadyMappedIsNotDownloaded() throws IOException {
		if (Channels.READY_FOR_PRODUCTION) return;
		Hash hash = my(Crypto.class).digest(new byte[]{ 42 }); 
		File expected = anySmallFile();
		my(FileMap.class).putFile(expected.getAbsolutePath(), expected.length(), expected.lastModified(), hash);

		@SuppressWarnings("unused")
		WeakContract contractToAvoidGc = my(RemoteTuples.class).addSubscription(FileRequest.class, new Consumer<FileRequest>() { @Override public void consume(FileRequest request) {
			throw new IllegalStateException();
		}});

		File actual = newTmpFile();
		_subject.startFileDownload(actual, expected.length(), expected.lastModified(), hash, remoteSeal());

		my(TupleDispatcher.class).waitForAllDispatchingToFinish();
		my(IO.class).files().assertSameContents(actual, expected);
	}


	private File anySmallFile() {
		return my(ClassUtils.class).classFile(getClass());
	}

}

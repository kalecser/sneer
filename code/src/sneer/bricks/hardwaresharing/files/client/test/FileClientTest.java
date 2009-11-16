package sneer.bricks.hardwaresharing.files.client.test;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.hardwaresharing.files.hasher.Hasher;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.hardwaresharing.files.protocol.FileContents;
import sneer.bricks.hardwaresharing.files.protocol.FileContentsFirstBlock;
import sneer.bricks.hardwaresharing.files.protocol.FileRequest;
import sneer.bricks.hardwaresharing.files.protocol.Protocol;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.Seal;
import sneer.foundation.lang.Consumer;

public class FileClientTest extends BrickTest {

	private File _classFile;

	@Test (timeout = 2000)
	public void fileAlreadyMappedIsNotDownloaded() throws IOException {
		final Sneer1024 hash = my(FileMap.class).put(myClassFile());
		my(TupleSpace.class).addSubscription(FileRequest.class, new Consumer<FileRequest>() { @Override public void consume(FileRequest request) {
			throw new IllegalStateException();
		}});

		final File tmpFile = createTmpFile("tmpFile");
		my(FileClient.class).fetchFile(tmpFile, hash);

		my(TupleSpace.class).waitForAllDispatchingToFinish();
		my(IO.class).files().assertSameContents(tmpFile, myClassFile());
	}

	@Ignore
	@Test (timeout = 3000)
	public void receiveFileBlocksOutOfSequence() throws IOException {
		final Iterator<FileContents> fileContents = fileContentBlocks().iterator();
		my(TupleSpace.class).addSubscription(FileRequest.class, new Consumer<FileRequest>() { @Override public void consume(FileRequest request) {
			my(TupleSpace.class).publish(fileContents.next());
		}});

		// 1) Fetch class file
		final File tmpFile = createTmpFile("tmpFile");
		my(FileClient.class).fetchFile(tmpFile, classFileHash());

		// 3) Assert file received is equals original class file
		my(TupleSpace.class).waitForAllDispatchingToFinish();
		my(IO.class).files().assertSameContents(tmpFile, myClassFile());
	}

	private Seal me() {
		return my(Seals.class).ownSeal();
	}

	private File myClassFile() {
		if (_classFile == null)
			_classFile = my(ClassUtils.class).classFile(getClass());

		return _classFile;
	}

	private Sneer1024 classFileHash() throws IOException {
		return my(Hasher.class).hash(myClassFile());
	}

	private ImmutableByteArray classFileBlock(int blockNumber) throws IOException {
		return my(ImmutableArrays.class).newImmutableByteArray(my(IO.class).files().readBlock(myClassFile(), blockNumber, Protocol.FILE_BLOCK_SIZE));
	}

	private List<FileContents> fileContentBlocks() throws IOException {
		List<FileContents> contentsInBlocks = new ArrayList<FileContents>();

		contentsInBlocks.add(new FileContentsFirstBlock(me(), classFileHash(), myClassFile().length(), classFileBlock(0), myClassFile().getName()));
		contentsInBlocks.add(new FileContents(me(), classFileHash(), 1, classFileBlock(1), myClassFile().getName()));
		contentsInBlocks.add(new FileContents(me(), classFileHash(), 2, classFileBlock(2), myClassFile().getName()));

		return contentsInBlocks;
	}

}

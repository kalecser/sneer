package sneer.bricks.hardwaresharing.files.client.test;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.hardwaresharing.files.hasher.Hasher;
import sneer.bricks.hardwaresharing.files.protocol.FileContents;
import sneer.bricks.hardwaresharing.files.protocol.FileContentsFirstBlock;
import sneer.bricks.hardwaresharing.files.protocol.FileRequest;
import sneer.bricks.hardwaresharing.files.protocol.Protocol;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.Seal;
import sneer.foundation.lang.Consumer;

public class FileDownloadTest extends BrickTest {

	@Test (timeout = 2000)
	public void receiveFileContentBlocksOutOfSequence() throws IOException {
		File smallFile = createFileWithRandomContent();
		Sneer1024 smallFileHash = my(Hasher.class).hash(smallFile);

		final Iterator<FileContents> fileContentBlocks = createFileContentBlocks(smallFile, smallFileHash).iterator();
		@SuppressWarnings("unused")
		WeakContract toAvoidGC = my(TupleSpace.class).addSubscription(FileRequest.class, new Consumer<FileRequest>() { @Override public void consume(FileRequest request) {
			my(TupleSpace.class).publish(fileContentBlocks.next());
		}});

		File tmpFile = createTmpFile("tmpFile");
		my(FileClient.class).fetchFile(tmpFile, smallFileHash);

		my(TupleSpace.class).waitForAllDispatchingToFinish();
		System.err.println("Small File Size: " + smallFile.length() + " Bs");
		System.err.println("Tmp File Size: " + tmpFile.length() + " Bs");
		my(IO.class).files().assertSameContents(tmpFile, smallFile);
	}

	private File createFileWithRandomContent() throws IOException {
		final File fileWithRandomContent = createTmpFile("fileWithRandomContent");
		final byte[] randomBytes = new byte[3 * Protocol.FILE_BLOCK_SIZE];
		new Random().nextBytes(randomBytes);
		my(IO.class).files().writeByteArrayToFile(fileWithRandomContent, randomBytes);

		return fileWithRandomContent;
	}

	private List<FileContents> createFileContentBlocks(File file, Sneer1024 fileHash) throws IOException {
		List<FileContents> blocks = new ArrayList<FileContents>();

		blocks.add(new FileContentsFirstBlock(me(), fileHash, file.length(), getFileBlock(file, 0), file.getName()));
		blocks.add(new FileContents(me(), fileHash, 1, getFileBlock(file, 1), file.getName()));
		blocks.add(new FileContents(me(), fileHash, 2, getFileBlock(file, 2), file.getName()));

		return blocks;
	}

	private Seal me() {
		return my(Seals.class).ownSeal();
	}

	private ImmutableByteArray getFileBlock(File file, int blockNumber) throws IOException {
		return my(ImmutableArrays.class).newImmutableByteArray(my(IO.class).files().readBlock(file, blockNumber, Protocol.FILE_BLOCK_SIZE));
	}

}

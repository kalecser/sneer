package sneer.bricks.expression.files.client.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.TimeoutException;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FileContents;
import sneer.bricks.expression.files.protocol.FileContentsFirstBlock;
import sneer.bricks.expression.files.protocol.FileRequest;
import sneer.bricks.expression.files.protocol.Protocol;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.pulp.keymanager.ContactSeals;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.lang.Consumer;

public class FileClientTest extends BrickTest {

	private final FileClient _subject = my(FileClient.class);;

	@Test (timeout = 3000)
	public void fileAlreadyMappedIsNotDownloaded() throws IOException {
		Sneer1024 hash = my(Crypto.class).digest(new byte[]{42}); 
		my(FileMap.class).putFile(anySmallFile(), hash);

		@SuppressWarnings("unused")
		WeakContract contractToAvoidGc = my(TupleSpace.class).addSubscription(FileRequest.class, new Consumer<FileRequest>() { @Override public void consume(FileRequest request) {
			throw new IllegalStateException();
		}});

		File tmpFile = newTmpFile();
		_subject.startFileDownload(tmpFile, hash);

		my(TupleSpace.class).waitForAllDispatchingToFinish();
		my(IO.class).files().assertSameContents(tmpFile, anySmallFile());
	}

	@Test (timeout = 4000)
	public void receiveFileContentBlocksOutOfSequence() throws IOException, TimeoutException {
		final File smallFile = createTmpFileWithRandomContent(3 * Protocol.FILE_BLOCK_SIZE);
		final Sneer1024 smallFileHash = my(Crypto.class).digest(smallFile);

		final Iterator<FileContents> blocksOutOfSequence = createFileContentBlocks(smallFile, smallFileHash).iterator();
		@SuppressWarnings("unused")
		WeakContract toAvoidGC = my(TupleSpace.class).addSubscription(FileRequest.class, new Consumer<FileRequest>() { @Override public void consume(FileRequest request) {
			my(TupleSpace.class).acquire(blocksOutOfSequence.next());
			blocksOutOfSequence.remove();
			my(Clock.class).advanceTime(1); // To avoid duplicated tuples
		}});

		final File tmpFile = newTmpFile();
		_subject.startFileDownload(tmpFile, smallFileHash).waitTillFinished();

		my(TupleSpace.class).waitForAllDispatchingToFinish();
		my(IO.class).files().assertSameContents(tmpFile, smallFile);
	}

	private File anySmallFile() {
		return my(ClassUtils.class).classFile(getClass());
	}

	private List<FileContents> createFileContentBlocks(File file, Sneer1024 fileHash) throws IOException {
		List<FileContents> blocks = new ArrayList<FileContents>();

		blocks.add(new FileContents(me(), fileHash, 1, getFileBlock(file, 1), file.getName())); // 2nd block
		blocks.add(new FileContentsFirstBlock(me(), fileHash, file.length(), getFileBlock(file, 0), file.getName())); // 1st block
		blocks.add(new FileContents(me(), fileHash, 2, getFileBlock(file, 2), file.getName())); // 3rd block

		return blocks;
	}

	private Seal me() {
		return my(ContactSeals.class).ownSeal();
	}

	private ImmutableByteArray getFileBlock(File file, int blockNumber) throws IOException {
		return new ImmutableByteArray(my(IO.class).files().readBlock(file, blockNumber, Protocol.FILE_BLOCK_SIZE));
	}

	private File createTmpFileWithRandomContent(int fileSizeInBytes) throws IOException {
		final File fileWithRandomContent = newTmpFile();
		final byte[] randomBytes = new byte[fileSizeInBytes];
		new Random().nextBytes(randomBytes);
		my(IO.class).files().writeByteArrayToFile(fileWithRandomContent, randomBytes);

		return fileWithRandomContent;
	}

}

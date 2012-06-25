package sneer.bricks.expression.files.client.tests;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import basis.environments.Environments;
import basis.lang.ClosureX;
import basis.lang.Consumer;
import basis.lang.arrays.ImmutableByteArray;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.TimeoutException;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FileContents;
import sneer.bricks.expression.files.protocol.FileContentsFirstBlock;
import sneer.bricks.expression.files.protocol.FileRequest;
import sneer.bricks.expression.files.protocol.Protocol;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.dispatcher.TupleDispatcher;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.software.code.classutils.ClassUtils;

public class FileClientTest extends BrickTestWithTuples {

	private final FileClient _subject = my(FileClient.class);

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;

	@Test (timeout = 3000)
	public void fileAlreadyMappedIsNotDownloaded() throws IOException {
		Hash hash = my(Crypto.class).digest(new byte[]{ 42 }); 
		File file = anySmallFile();
		my(FileMap.class).putFile(file.getAbsolutePath(), file.lastModified(), hash);

		@SuppressWarnings("unused")
		WeakContract contractToAvoidGc = my(RemoteTuples.class).addSubscription(FileRequest.class, new Consumer<FileRequest>() { @Override public void consume(FileRequest request) {
			throw new IllegalStateException();
		}});

		File tmpFile = newTmpFile();
		_subject.startFileDownload(tmpFile, tmpFile.lastModified(), hash, remoteSeal());

		my(TupleDispatcher.class).waitForAllDispatchingToFinish();
		my(IO.class).files().assertSameContents(tmpFile, file);
	}

	
	@Test (timeout = 4000, expected = IOException.class)
	public void receiveFileThatDoesntMatchExpectedHash() throws IOException, TimeoutException {
		final Hash wrongHash = new Hash(new ImmutableByteArray(new byte[]{ 42 }));
		final File smallFile = createTmpFileWithRandomContent(3 * Protocol.FILE_BLOCK_SIZE);

		receiveInRandomOrder(wrongHash, smallFile);
	}

	
	@Test (timeout = 4000)
	public void receiveFileContentBlocksOutOfSequence() throws IOException, TimeoutException {
		final File smallFile = createTmpFileWithRandomContent(3 * Protocol.FILE_BLOCK_SIZE);
		final Hash fileHash = my(Crypto.class).digest(smallFile);

		receiveInRandomOrder(fileHash, smallFile);
	}

	
	private void receiveInRandomOrder(final Hash fileHash, final File smallFile)	throws IOException, TimeoutException {
		final Seal addressee = my(OwnSeal.class).get().currentValue();
		Environments.runWith(remote(my(Clock.class)), new ClosureX<IOException>() { @Override public void run() throws IOException {
			final Iterator<FileContents> blocksOutOfSequence = createFileContentBlocks(addressee, smallFile, fileHash).iterator();
			sendFileContentBlocksUponRequest(blocksOutOfSequence);
		}});

		final File tmpFile = newTmpFile();
		_subject.startFileDownload(tmpFile, tmpFile.lastModified(), fileHash, remoteSeal())
			.waitTillFinished();

		my(IO.class).files().assertSameContents(tmpFile, smallFile);
	}

	private File anySmallFile() {
		return my(ClassUtils.class).classFile(getClass());
	}

	private List<FileContents> createFileContentBlocks(Seal addressee, File file, Hash fileHash) throws IOException {
		List<FileContents> blocks = new ArrayList<FileContents>();

		blocks.add(new FileContents(addressee, fileHash, 1, getFileBlock(file, 1), file.getName())); // 2nd block
		blocks.add(new FileContentsFirstBlock(addressee, fileHash, file.length(), getFileBlock(file, 0), file.getName())); // 1st block
		blocks.add(new FileContents(addressee, fileHash, 2, getFileBlock(file, 2), file.getName())); // 3rd block

		return blocks;
	}

	private void sendFileContentBlocksUponRequest(final Iterator<FileContents> blocks) {
		_toAvoidGC = my(RemoteTuples.class).addSubscription(FileRequest.class, new Consumer<FileRequest>() { @Override public void consume(FileRequest request) {
			my(TupleSpace.class).add(blocks.next());
			my(Clock.class).advanceTime(1); // To avoid duplicated tuples
		}});
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

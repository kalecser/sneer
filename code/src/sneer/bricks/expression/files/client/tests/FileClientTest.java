package sneer.bricks.expression.files.client.tests;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.TimeoutException;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FileContents;
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
import basis.environments.Environments;
import basis.lang.ClosureX;
import basis.lang.Consumer;
import basis.lang.arrays.ImmutableByteArray;

public class FileClientTest extends BrickTestWithTuples {

	private final FileClient _subject = my(FileClient.class);

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;

	@Test (timeout = 3000)
	public void fileAlreadyMappedIsNotDownloaded() throws IOException {
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


	@Ignore
	@Test (timeout = 4000, expected = IOException.class)
	public void receiveFileThatDoesntMatchExpectedHash() throws IOException, TimeoutException {
		fail("Move to test hat uses server and simply map the file with a different hash.");
		final Hash wrongHash = new Hash(new ImmutableByteArray(new byte[]{ 42 }));
		final File smallFile = createTmpFileWithRandomContent(3 * Protocol.FILE_BLOCK_SIZE);

		final Seal addressee = my(OwnSeal.class).get().currentValue();
		Environments.runWith(remote(my(Clock.class)), new ClosureX<IOException>() { @Override public void run() throws IOException {
			final List<FileContents> blocks = createFileContentBlocks(addressee, smallFile, wrongHash);
			sendFileContentBlocksUponRequest(blocks);
		}});
		
		final File tmpFile = newTmpFile();
		_subject.startFileDownload(tmpFile, smallFile.length(), smallFile.lastModified(), wrongHash, remoteSeal())
			.waitTillFinished();
		
		my(IO.class).files().assertSameContents(tmpFile, smallFile);
	}

	
	private File anySmallFile() {
		return my(ClassUtils.class).classFile(getClass());
	}

	private List<FileContents> createFileContentBlocks(Seal addressee, File file, Hash fileHash) throws IOException {
		List<FileContents> ret = new ArrayList<FileContents>();

		ret.add(new FileContents(addressee, fileHash, 0, getFileBlock(file, 0), file.getName()));
		ret.add(new FileContents(addressee, fileHash, 1, getFileBlock(file, 1), file.getName()));
		ret.add(new FileContents(addressee, fileHash, 2, getFileBlock(file, 2), file.getName()));

		return ret;
	}

	private void sendFileContentBlocksUponRequest(final List<FileContents> blocks) {
		_toAvoidGC = my(RemoteTuples.class).addSubscription(FileRequest.class, new Consumer<FileRequest>() { @Override public void consume(FileRequest request) {
			my(TupleSpace.class).add(blocks.get(request.blockNumbers.iterator().next()));
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

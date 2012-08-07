package sneer.bricks.expression.files.tests;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.TimeoutException;
import sneer.bricks.expression.files.protocol.Protocol;
import sneer.bricks.expression.files.server.FileServer;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.ticker.custom.CustomClockTicker;
import sneer.bricks.hardware.cpu.codec.Codec;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.io.files.atomic.dotpart.DotParts;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.ClosureX;

public class RemoteCopyTest extends FileCopyTestBase {

	private Seal _localSeal = my(OwnSeal.class).get().currentValue();
	private final Environment remote = remote(my(Clock.class));


	@Test (timeout = 3000)
	public void partialDownloadRecoveryWithSizeDivisibleByBlock() throws Exception {
		int size = Protocol.FILE_BLOCK_SIZE * 5;
		partialDownloadRecovery(size);
	}

	@Test (timeout = 3000)
	public void partialDownloadRecoveryWithSizeNotDivisibleByBlock() throws Exception {
		int size = Protocol.FILE_BLOCK_SIZE * 5 + 1;
		partialDownloadRecovery(size);
	}


	private void partialDownloadRecovery(int size) throws Exception {
		File file = newTmpFile();
		writePseudoRandomBytesTo(file, Protocol.FILE_BLOCK_SIZE * 10);
		File part = simulatePartialTransfer(file, size);
		testWith(file);
		assertFalse(part.exists());
	}

	
	@Override
	protected void copyFileFromFileMap(final Hash hashOfContents, final long size, final long lastModified, final File destination) throws Exception {
		copyFromFileMap(new ClosureX<Exception>() { @Override public void run() throws IOException, TimeoutException {
			Download download = my(FileClient.class).startFileDownload(destination, size, lastModified, hashOfContents, _localSeal);
			download.waitTillFinished();
		}});
	}

	
	private File simulatePartialTransfer(File file, int size) throws Exception {
		Hash hash =	hash(file);
		File ret = my(DotParts.class).newDotPartFor(newTmpFile(), "downloading-" + hex(hash));
		writePseudoRandomBytesTo(ret, size);
		return ret;
	}
	
	
	@Override
	protected void copyFolderFromFileMap(final Hash hashOfContents, final File destination) throws Exception {
		copyFromFileMap(new ClosureX<Exception>() { @Override public void run() throws IOException, TimeoutException {
			Download download = my(FileClient.class).startFolderDownload(destination, hashOfContents, _localSeal);
			download.waitTillFinished();
		}});
	}


	private void copyFromFileMap(ClosureX<Exception> closure) throws Exception {
		my(FileServer.class);
		avoidDuplicateTuples();
		Environments.runWith(remote, closure);
	}


	private void avoidDuplicateTuples() {
		my(CustomClockTicker.class).start(10, 1);
	}

	
	private static String hex(Hash hash) {
		return my(Codec.class).hex().encode(hash.bytes.copy());
	}

}

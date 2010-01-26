package sneer.bricks.expression.files.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.server.FileServer;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.ticker.custom.CustomClockTicker;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.ClosureX;

public class RemoteCopyTest extends FileCopyTestBase {

	@Override
	protected void copyFileFromFileMap(final Sneer1024 hashOfContents, final File destination) throws IOException {
		copyFromFileMap(new ClosureX<IOException>() { @Override public void run() throws IOException {
			my(FileClient.class).startFileDownload(destination, hashOfContents).waitTillFinished();
		}});
	}

	
	@Override
	protected void copyFolderFromFileMap(final Sneer1024 hashOfContents, final File destination) throws IOException {
		try {
			tryToCopyFolderFromFileMap(hashOfContents, destination);
		} catch (RuntimeException e) {
			throw new RuntimeException("Is this a timeout? It might have been caused by running on a fast machine and producing several equal tuples (there are duplicate files and directories in the fixtures) on the same clock tick and the TupleSpace ignores them. In this case, something has to be done about it.", e);
		}
	}

	
	private void tryToCopyFolderFromFileMap(final Sneer1024 hashOfContents,	final File destination) throws IOException {
		copyFromFileMap(new ClosureX<IOException>() { @Override public void run() throws IOException {
			my(FileClient.class).startFolderDownload(destination, hashOfContents).waitTillFinished();
		}});
	}

	
	private void copyFromFileMap(ClosureX<IOException> closure) throws IOException {
		@SuppressWarnings("unused") FileServer server = my(FileServer.class);
		my(CustomClockTicker.class).start(10, 15000);
		Environment remote = newTestEnvironment(my(TupleSpace.class), my(Clock.class));
		configureStorageFolder(remote);
		
		Environments.runWith(remote, closure);
		
		crash(remote);
	}

	
	private void configureStorageFolder(Environment remote) {
		Environments.runWith(remote, new Closure() { @Override public void run() {
			my(FolderConfig.class).storageFolder().set(newTmpFile("remote"));
		}});
	}

	
	private void crash(Environment remote) {
		Environments.runWith(remote, new Closure() { @Override public void run() {
			my(Threads.class).crashAllThreads();
		}});
	}
}

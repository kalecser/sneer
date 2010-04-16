package sneer.bricks.expression.files.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.TimeoutException;
import sneer.bricks.expression.files.server.FileServer;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.ticker.custom.CustomClockTicker;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ClosureX;

public class RemoteCopyTest extends FileCopyTestBase {

	@Override
	protected void copyFileFromFileMap(final Hash hashOfContents, final File destination) throws Exception {
		copyFromFileMap(new ClosureX<Exception>() { @Override public void run() throws IOException, TimeoutException {
			my(FileClient.class).startFileDownload(destination, hashOfContents).waitTillFinished();
		}});
	}


	@Override
	protected void copyFolderFromFileMap(final Hash hashOfContents, final File destination) throws Exception {
		copyFromFileMap(new ClosureX<Exception>() { @Override public void run() throws IOException, TimeoutException {
			my(FileClient.class).startFolderDownload(destination, hashOfContents).waitTillFinished();
		}});
	}


	private void copyFromFileMap(ClosureX<Exception> closure) throws Exception {
		@SuppressWarnings("unused") FileServer server = my(FileServer.class);
		avoidDuplicateTuples();
		Environment remote = newTestEnvironment(my(TupleSpace.class), my(Clock.class));
		configureStorageFolder(remote, "remote/Data");

		Environments.runWith(remote, closure);
		crash(remote);
	}


	private void avoidDuplicateTuples() {
		my(CustomClockTicker.class).start(10, 1);
	}

}

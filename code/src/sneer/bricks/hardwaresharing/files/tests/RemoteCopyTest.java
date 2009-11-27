package sneer.bricks.hardwaresharing.files.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.ticker.ClockTicker;
import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.hardwaresharing.files.server.FileServer;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.own.name.OwnNameKeeper;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;

public class RemoteCopyTest extends FileCopyTestBase {

	@Override
	protected void copyFileFromFileMap(final Sneer1024 hashOfContents, final File destination) throws IOException {
		copyFromFileMap(new Closure<IOException>() { @Override public void run() throws IOException {
			my(FileClient.class).startFileDownload(destination, hashOfContents).waitTillFinished();
		}});
	}

	@Override
	protected void copyFolderFromFileMap(final Sneer1024 hashOfContents, final File destination) throws IOException {
		copyFromFileMap(new Closure<IOException>() { @Override public void run() throws IOException {
			my(FileClient.class).startFolderDownload(destination, hashOfContents).waitTillFinished();
		}});
	}

	private void copyFromFileMap(Closure<IOException> closure) throws IOException {
		@SuppressWarnings("unused") FileServer server = my(FileServer.class);
		my(ClockTicker.class);
		Environment remote = newTestEnvironment(my(TupleSpace.class), my(OwnNameKeeper.class), my(Clock.class));
		Environments.runWith(remote, closure);
	}

}

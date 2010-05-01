package sneer.bricks.expression.files.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.TimeoutException;
import sneer.bricks.expression.files.server.FileServer;
import sneer.bricks.expression.tuples.testsupport.pump.TuplePump;
import sneer.bricks.expression.tuples.testsupport.pump.TuplePumps;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.ticker.custom.CustomClockTicker;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ClosureX;

public class RemoteCopyTest extends FileCopyTestBase {

	@SuppressWarnings("unused")	private TuplePump _refToAvoidGc;


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
		my(FileServer.class);

		avoidDuplicateTuples();

		Environment remote = configureRemoteEnvironment();
		Environments.runWith(remote, closure);
		crash(remote);
	}


	private Environment configureRemoteEnvironment() {
		Environment remote = newTestEnvironment(my(Clock.class));
		configureStorageFolder(remote, "remote/Data");
		_refToAvoidGc = my(TuplePumps.class).startPumpingWith(remote);
		return remote;
	}


	private void avoidDuplicateTuples() {
		my(CustomClockTicker.class).start(10, 1);
	}

}

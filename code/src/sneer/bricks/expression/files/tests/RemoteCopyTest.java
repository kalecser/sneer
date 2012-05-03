package sneer.bricks.expression.files.tests;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.client.downloads.TimeoutException;
import sneer.bricks.expression.files.server.FileServer;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.ticker.custom.CustomClockTicker;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import basis.environments.Environments;
import basis.lang.ClosureX;

public class RemoteCopyTest extends FileCopyTestBase {

	private Seal _localSeal = my(OwnSeal.class).get().currentValue();

	@Override
	protected void copyFileFromFileMap(final Hash hashOfContents, final File destination) throws Exception {
		copyFromFileMap(new ClosureX<Exception>() { @Override public void run() throws IOException, TimeoutException {
			Download download = my(FileClient.class).startFileDownload(destination, -1, hashOfContents, _localSeal);
			download.waitTillFinished();
		}});
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
		Environments.runWith(remote(my(Clock.class)), closure);
	}


	private void avoidDuplicateTuples() {
		my(CustomClockTicker.class).start(10, 1);
	}

}

package dfcsantos.tracks.exchange.endorsements.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;
import sneer.foundation.util.concurrent.Latch;
import dfcsantos.tracks.exchange.endorsements.TrackEndorsement;
import dfcsantos.tracks.exchange.endorsements.TrackEndorser;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

public class TrackEndorserTest extends BrickTestWithTuples {

	@Bind private final FileMap _fileMap = mock(FileMap.class);

	@Test(timeout = 4000)
	public void trackEndorsements() throws IOException {
		final File subfolder = new File(tmpFolder(),"rocknroll");
		final File track = new File(subfolder,"thunderstruck.mp3");

		assertTrue(subfolder.mkdir());
		assertTrue(track.createNewFile());

		final Hash hash = my(Crypto.class).digest(track);
		final Latch latch = new Latch();

		@SuppressWarnings("unused")
		WeakContract refToAvoidGC = my(RemoteTuples.class).addSubscription(TrackEndorsement.class, new Consumer<TrackEndorsement>() { @Override public void consume(TrackEndorsement trackEndorsement) {
			assertEquals("rocknroll/thunderstruck.mp3", trackEndorsement.path);
			assertEquals(hash, trackEndorsement.hash);
			latch.open();
		}});

		checking(new Expectations(){{
			oneOf(_fileMap).getHash(track.getAbsolutePath()); will(returnValue(hash));
		}});

		Environments.runWith(remote(), new Closure() { @Override public void run() {
			my(TracksFolderKeeper.class).setTracksFolder(tmpFolderName());
			activateTrackEndorser();
		}});

		latch.waitTillOpen();
	}

	private void activateTrackEndorser() {
		my(TrackEndorser.class).setOnOffSwitch(my(Signals.class).constant(true));
	}

}

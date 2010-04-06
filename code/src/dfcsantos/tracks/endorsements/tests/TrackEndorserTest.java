package dfcsantos.tracks.endorsements.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.endorsements.protocol.TrackEndorsement;
import dfcsantos.tracks.endorsements.server.TrackEndorser;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

public class TrackEndorserTest extends BrickTest {

	@Bind private final FileMap _fileMap = mock(FileMap.class);

	@Test(timeout = 4000)
	public void trackEndorsements() throws IOException {
		final File subfolder = new File(tmpFolder(),"rocknroll");
		final File track = new File(subfolder,"thunderstruck.mp3");

		assertTrue(subfolder.mkdir());
		assertTrue(track.createNewFile());

		final Hash hash = my(Crypto.class).digest(track);
		final Latch latch = my(Latches.class).produce();

		@SuppressWarnings("unused")
		WeakContract refToAvoidGC = my(TupleSpace.class).addSubscription(TrackEndorsement.class, new Consumer<TrackEndorsement>() { @Override public void consume(TrackEndorsement trackEndorsement) {
			assertEquals("rocknroll/thunderstruck.mp3", trackEndorsement.path);
			assertEquals(hash, trackEndorsement.hash);
			latch.open();
		}});

		checking(new Expectations(){{
			oneOf(_fileMap).getFile(hash); will(returnValue(track));
		}});

		my(TracksFolderKeeper.class).setSharedTracksFolder(tmpFolder());
		activateTrackEndorser();
		latch.waitTillOpen();
	}

	private void activateTrackEndorser() {
		my(TrackEndorser.class).setOnOffSwitch(my(Signals.class).constant(true));
	}

}

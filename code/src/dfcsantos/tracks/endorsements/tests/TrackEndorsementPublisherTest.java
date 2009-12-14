package dfcsantos.tracks.endorsements.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.hardware.cpu.algorithms.crypto.Crypto;
import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.endorsements.TrackEndorsement;
import dfcsantos.tracks.endorsements.TrackEndorser;
import dfcsantos.tracks.folder.keeper.TracksFolderKeeper;

public class TrackEndorsementPublisherTest extends BrickTest {

	@Bind private final FileMap _fileMap = mock(FileMap.class);

	@Test(timeout = 4000)
	public void trackEndorsements() throws IOException {
		final File subfolder = new File(tmpFolder(),"rocknroll");
		final File track = new File(subfolder,"thunderstruck.mp3");

		assertTrue(subfolder.mkdir());
		assertTrue(track.createNewFile());

		final Sneer1024 hash = my(Crypto.class).digest(track);
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
		my(TrackEndorser.class);
		latch.waitTillOpen();
	}

}

package dfcsantos.tracks.endorsements.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.endorsements.TrackEndorsement;
import dfcsantos.tracks.endorsements.TrackEndorsementPublisher;
import dfcsantos.tracks.folder.OwnTracksFolderKeeper;

public class TrackEndorsementPublisherTest extends BrickTest {

	@Test(timeout = 4000)
	public void trackEndorsements() throws IOException {
		assertTrue(new File(tmpFolder(),"subfolder").mkdir());
		assertTrue(new File(tmpFolder(),"subfolder/foo.mp3").createNewFile());
		final Latch latch = my(Latches.class).produce();
		@SuppressWarnings("unused")
		WeakContract refToAvoidGC = my(TupleSpace.class).addSubscription(TrackEndorsement.class, new Consumer<TrackEndorsement>() {@Override public void consume(TrackEndorsement trackEndorsement) {
			assertTrue(trackEndorsement.path.indexOf("foo.mp3")!=-1);
			latch.open();
		}});
		my(OwnTracksFolderKeeper.class).setOwnTracksFolder(tmpFolder());
		my(TrackEndorsementPublisher.class);
		latch.waitTillOpen();
	}

}

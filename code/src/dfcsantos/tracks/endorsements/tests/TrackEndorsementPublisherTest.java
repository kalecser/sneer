package dfcsantos.tracks.endorsements.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.endorsements.TrackEndorsement;
import dfcsantos.tracks.endorsements.TrackEndorser;
import dfcsantos.tracks.folder.TracksFolderKeeper;

public class TrackEndorsementPublisherTest extends BrickTest {

	@Bind private final FileMap _fileMap = mock(FileMap.class);
	
	@Test(timeout = 4000)
	public void trackEndorsements() throws IOException {
		final File subfolder = new File(tmpFolder(),"rocknroll");
		final File track = new File(subfolder,"thunderstruck.mp3");
		final File peerTrackFolder = new File(my(FolderConfig.class).tmpFolderFor(TracksFolderKeeper.class), "peertracks");
		final File defaultSharedTrackFolder = new File(my(FolderConfig.class).storageFolder().get() ,"media/tracks");
		
		assertTrue(subfolder.mkdir());
		assertTrue(track.createNewFile());

		checking(new Expectations(){{
			oneOf(_fileMap).put(peerTrackFolder);
			oneOf(_fileMap).put(defaultSharedTrackFolder);
			oneOf(_fileMap).put(tmpFolder());
			oneOf(_fileMap).put(track);
		}});
		
		final Latch latch = my(Latches.class).produce();
		@SuppressWarnings("unused")
		WeakContract refToAvoidGC = my(TupleSpace.class).addSubscription(TrackEndorsement.class, new Consumer<TrackEndorsement>() {@Override public void consume(TrackEndorsement trackEndorsement) {
			assertEquals("rocknroll/thunderstruck.mp3", trackEndorsement.path);
			latch.open();
		}});
		my(TracksFolderKeeper.class).setSharedTracksFolder(tmpFolder());
		my(TrackEndorser.class);
		latch.waitTillOpen();
	}

}

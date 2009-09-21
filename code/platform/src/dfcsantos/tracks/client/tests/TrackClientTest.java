package dfcsantos.tracks.client.tests;

import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.hardwaresharing.files.writer.FileWriter;
import sneer.bricks.pulp.crypto.Crypto;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import dfcsantos.tracks.announcer.TrackAnnouncement;
import dfcsantos.tracks.client.TrackClient;
import dfcsantos.tracks.folder.OwnTracksFolderKeeper;

public class TrackClientTest extends BrickTest {

	@Bind private final FileClient _fileClient = mock(FileClient.class);
	@SuppressWarnings("unused")
	@Bind private final FileWriter _fileWriter = mock(FileWriter.class);

	
	@Test
	public void trackDownload() {
		final Sneer1024 hash1 = my(Crypto.class).digest(new byte[]{1});
		checking(new Expectations(){{
			exactly(1).of(_fileClient).fetchToCache(hash1);
			//exactly(1).of(_fileWriter).writeAtomicallyTo(..., lastModified, hashOfContents);
		}});
		my(OwnTracksFolderKeeper.class).setOwnTracksFolder(tmpFolder());
		my(TrackClient.class);
		my(TupleSpace.class).publish(new TrackAnnouncement("songs/subfolder/foo.mp3", hash1));
		my(TupleSpace.class).waitForAllDispatchingToFinish();
		
	}
}

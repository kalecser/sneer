package dfcsantos.tracks.client.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.network.social.ContactManager;
import sneer.bricks.pulp.crypto.Crypto;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import dfcsantos.tracks.client.TrackClient;
import dfcsantos.tracks.client.TrackEndorsement;
import dfcsantos.tracks.folder.TracksFolderKeeper;

public class TrackClientTest extends BrickTest {

	@Bind private final FileMap _fileMap = mock(FileMap.class);
	@Bind private final FileClient _fileClient = mock(FileClient.class);
	
	@Test(timeout = 4000)
	public void trackDownload() throws IOException {
		final Sneer1024 hash1 = my(Crypto.class).digest(new byte[]{1});
		
		checking(new Expectations(){{
			exactly(1).of(_fileMap).put(peerTracksFolder());
			exactly(1).of(_fileMap).put(shareTracksFolderDefaultValue());
			exactly(1).of(_fileClient).startFileDownload(new File(peerTracksFolder(), "foo.mp3"), 42, hash1);
		}});
		
		my(TrackClient.class);
		
		aquireEndorsementTuple(hash1, 42, "songs/subfolder/foo.mp3");
		my(TupleSpace.class).waitForAllDispatchingToFinish();
	}

	private File peerTracksFolder() {
		return new File(my(FolderConfig.class).tmpFolderFor(TracksFolderKeeper.class), "peertracks");
	}

	private File shareTracksFolderDefaultValue() {
		return new File(my(FolderConfig.class).storageFolder().get(), "media/tracks");
	}

	private void aquireEndorsementTuple(final Sneer1024 hash1, int lastModified, String track) {
		TrackEndorsement trackEndorsement = new TrackEndorsement(track, lastModified, hash1);
		stamp(trackEndorsement, "Someone Else");
		my(TupleSpace.class).acquire(trackEndorsement);
	}

	private void stamp(TrackEndorsement trackEndorsement, String contact) {
		trackEndorsement.stamp(my(Seals.class).sealGiven(my(ContactManager.class).produceContact(contact)), 1234);
	}

}

package dfcsantos.tracks.client.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import org.jmock.Expectations;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.hardwaresharing.files.protocol.FileRequest;
import sneer.bricks.network.social.ContactManager;
import sneer.bricks.pulp.crypto.Crypto;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.client.TrackClient;
import dfcsantos.tracks.client.TrackEndorsement;
import dfcsantos.tracks.folder.TracksFolderKeeper;
import dfcsantos.wusic.Wusic;

public class TrackClientTest extends BrickTest {

	@Bind private final FileMap _fileMap = mock(FileMap.class);
	@Bind private final FileClient _fileClient = mock(FileClient.class);

	@Test(timeout = 2000)
	public void trackDownload() throws Exception {
		final Sneer1024 hash1 = my(Crypto.class).digest(new byte[] { 1 });

		checking(new Expectations(){{
			exactly(1).of(_fileMap).put(peerTracksFolder());
			exactly(1).of(_fileMap).put(shareTracksFolderDefaultValue());
			exactly(1).of(_fileClient).startFileDownload(new File(peerTracksFolder(), "foo.mp3"), 42, hash1);
		}});

		// Allowing 1 MB is enough, since peerTracksFolder is empty
		my(Wusic.class).enableTracksDownload();
		my(Wusic.class).tracksDownloadAllowanceSetter().consume(1);

		my(TrackClient.class);

		aquireEndorsementTuple(hash1, 42, "songs/subfolder/foo.mp3");
		my(TupleSpace.class).waitForAllDispatchingToFinish();
	}

	@Ignore
	@Test (timeout = 2000)
	public void tryToDowloadTrackWithoutSettingAllowance() throws Exception {
		final Sneer1024 hash1 = my(Crypto.class).digest(new byte[] { 1 });
		checking(new Expectations(){{
			exactly(1).of(_fileMap).put(peerTracksFolder());
			exactly(1).of(_fileMap).put(shareTracksFolderDefaultValue());
		}});

		my(TupleSpace.class).addSubscription(FileRequest.class, new Consumer<FileRequest>() { @Override public void consume(FileRequest request) {
			fail();
		}});

		my(TrackClient.class);

		aquireEndorsementTuple(hash1, 42, "songs/subfolder/foo.mp3");
	}

	@Ignore
	@Test (timeout = 2000)
	public void tryToDownloadTrackWithAllowanceAlreadyReached() throws Exception {
		final Sneer1024 hash1 = my(Crypto.class).digest(new byte[] { 1 });
		checking(new Expectations(){{
			exactly(1).of(_fileMap).put(peerTracksFolder());
			exactly(1).of(_fileMap).put(shareTracksFolderDefaultValue());
		}});

		final File fileWith1MB = createTmpFileWithRandomContent(1048576);
		my(IO.class).files().copyFileToFolder(fileWith1MB, peerTracksFolder());

		my(Wusic.class).enableTracksDownload();
		my(Wusic.class).tracksDownloadAllowanceSetter().consume(1); // 1 MB

		my(TupleSpace.class).addSubscription(FileRequest.class, new Consumer<FileRequest>() { @Override public void consume(FileRequest request) {
			fail();
		}});

		my(TrackClient.class);

		aquireEndorsementTuple(hash1, 42, "songs/subfolder/foo.mp3");
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

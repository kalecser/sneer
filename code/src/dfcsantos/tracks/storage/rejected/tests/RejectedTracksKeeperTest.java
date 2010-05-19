package dfcsantos.tracks.storage.rejected.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.testsupport.BrickTestWithFiles;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.EnvironmentUtils;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ClosureX;
import dfcsantos.tracks.execution.playlist.Playlist;
import dfcsantos.tracks.execution.playlist.Playlists;
import dfcsantos.tracks.storage.rejected.RejectedTracksKeeper;

public class RejectedTracksKeeperTest extends BrickTestWithFiles {

	private RejectedTracksKeeper _subject = my(RejectedTracksKeeper.class);

	private final Playlist _playlist = my(Playlists.class).newSequentialPlaylist(tmpFolder()); 

	@Before
	public void createTrackFiles() throws IOException {
		createTmpFilesWithFileNameAsContent("track1.mp3", "track2.mp3", "track3.mp3");
	}

	@Test
	public void testTrackRejection() throws Exception {
		// Reject 1st track from playlist
		Hash firstTrackHash = my(Crypto.class).digest(_playlist.nextTrack().file());
		_subject.reject(firstTrackHash);
		assertTrue(_subject.isRejected(firstTrackHash));

		// Loop through playlist and check that only the first track were rejected
		Hash secondTrackHash = my(Crypto.class).digest(_playlist.nextTrack().file()); // 2nd track
		assertFalse(_subject.isRejected(secondTrackHash));

		Hash thirdTrackHash = my(Crypto.class).digest(_playlist.nextTrack().file()); // 3rd track
		assertFalse(_subject.isRejected(thirdTrackHash));

		firstTrackHash = my(Crypto.class).digest(_playlist.nextTrack().file()); // 1st track again
		assertTrue(_subject.isRejected(firstTrackHash));
	}

	@Test
	public void testRejectedTracksPersistence() throws Exception {
		final Hash firstTrackHash = my(Crypto.class).digest(_playlist.nextTrack().file()); // 1st track
		_subject.reject(firstTrackHash);

		final Hash secondTrackHash = my(Crypto.class).digest(_playlist.nextTrack().file()); // 2nd track
		_subject.reject(secondTrackHash);

		final Hash thirdTrackHash = my(Crypto.class).digest(_playlist.nextTrack().file()); // 3rd track
		_subject.reject(thirdTrackHash);

		Environment newTestEnvironment = newTestEnvironment(my(FolderConfig.class));
		_subject = EnvironmentUtils.retrieveFrom(newTestEnvironment, RejectedTracksKeeper.class);

		Environments.runWith(newTestEnvironment, new ClosureX<IOException>() { @Override public void run() {
			assertTrue(_subject.isRejected(firstTrackHash));
			assertTrue(_subject.isRejected(secondTrackHash));
			assertTrue(_subject.isRejected(thirdTrackHash));
		}});

		crash(newTestEnvironment);
	}

}

package dfcsantos.tracks.storage.rejected.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.EnvironmentUtils;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ClosureX;
import dfcsantos.tracks.execution.playlist.Playlist;
import dfcsantos.tracks.execution.playlist.Playlists;
import dfcsantos.tracks.storage.rejected.RejectedTracksKeeper;

public class RejectedTracksKeeperTest extends BrickTest {

	private RejectedTracksKeeper _subject = my(RejectedTracksKeeper.class);

	private final Playlist _playlist = my(Playlists.class).newSequentialPlaylist(tmpFolder()); 

	@Before
	public void createTrackFiles() throws IOException {
		createTmpFilesWithFileNameAsContent("track1.mp3", "track2.mp3", "track3.mp3");
	}

	@Test
	public void testTrackRejection() throws Exception {
		// Reject 1st track from playlist
		Sneer1024 firstTrackHash = my(Crypto.class).digest(_playlist.nextTrack().file());
		_subject.reject(firstTrackHash);
		assertTrue(_subject.isRejected(firstTrackHash));

		// Loop through playlist and check that only the first track were rejected
		Sneer1024 secondTrackHash = my(Crypto.class).digest(_playlist.nextTrack().file()); // 2nd track
		assertFalse(_subject.isRejected(secondTrackHash));

		Sneer1024 thirdTrackHash = my(Crypto.class).digest(_playlist.nextTrack().file()); // 3rd track
		assertFalse(_subject.isRejected(thirdTrackHash));

		firstTrackHash = my(Crypto.class).digest(_playlist.nextTrack().file()); // 1st track again
		assertTrue(_subject.isRejected(firstTrackHash));
	}

	@Test
	public void testRejectedTracksPersistence() throws Exception {
		final Sneer1024 firstTrackHash = my(Crypto.class).digest(_playlist.nextTrack().file()); // 1st track
		_subject.reject(firstTrackHash);

		final Sneer1024 secondTrackHash = my(Crypto.class).digest(_playlist.nextTrack().file()); // 2nd track
		_subject.reject(secondTrackHash);

		final Sneer1024 thirdTrackHash = my(Crypto.class).digest(_playlist.nextTrack().file()); // 3rd track
		_subject.reject(thirdTrackHash);

		Environment newTestEnvironment = newTestEnvironment(my(FolderConfig.class));
		_subject = EnvironmentUtils.retrieveFrom(newTestEnvironment, RejectedTracksKeeper.class);

		Environments.runWith(newTestEnvironment, new ClosureX<IOException>() { @Override public void run() {
			assertTrue(_subject.isRejected(firstTrackHash));
			assertTrue(_subject.isRejected(secondTrackHash));
			assertTrue(_subject.isRejected(thirdTrackHash));
		}});
	}

}

package dfcsantos.tracks.rejected.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.EnvironmentUtils;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.playlist.Playlist;
import dfcsantos.tracks.playlist.Playlists;
import dfcsantos.tracks.rejected.RejectedTracksKeeper;

public class RejectedTracksKeeperTest extends BrickTest {

	private RejectedTracksKeeper _subject = my(RejectedTracksKeeper.class);

	private final Playlist _playlist = my(Playlists.class).newSequentialPlaylist(tmpFolder()); 

	@Before
	public void createTrackFiles() throws IOException {
		createTmpFilesWithPathAsContent("track1.mp3", "track2.mp3", "track3.mp3");
	}

	@Test
	public void testTrackRejection() throws Exception {
		// Reject 1st track from playlist
		Track track = _playlist.nextTrack();
		_subject.reject(track.hash());
		assertTrue(_subject.isRejected(track.hash()));

		// Loop through playlist
		assertFalse(_subject.isRejected(_playlist.nextTrack().hash()));	// 2nd track
		assertFalse(_subject.isRejected(_playlist.nextTrack().hash()));	// 3rd track
		assertTrue(_subject.isRejected(_playlist.nextTrack().hash()));	// 1st track again
	}

	@Test
	public void testRejectedTracksPersistence() throws Exception {
		_subject.reject(_playlist.nextTrack().hash());
		_subject.reject(_playlist.nextTrack().hash());
		_subject.reject(_playlist.nextTrack().hash());

		Environment newTestEnvironment = newTestEnvironment(my(FolderConfig.class));
		_subject = EnvironmentUtils.retrieveFrom(newTestEnvironment, RejectedTracksKeeper.class);

		Environments.runWith(newTestEnvironment, new Closure<IOException>() { @Override public void run() {
			assertTrue(_subject.isRejected(_playlist.nextTrack().hash()));
			assertTrue(_subject.isRejected(_playlist.nextTrack().hash()));
			assertTrue(_subject.isRejected(_playlist.nextTrack().hash()));
		}});
	}

}

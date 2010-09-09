package dfcsantos.tracks.execution.playlist.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import dfcsantos.tracks.execution.playlist.Playlist;
import dfcsantos.tracks.execution.playlist.Playlists;

public class PlaylistTest extends BrickTestBase {

	private final Playlists _playlistFactory = my(Playlists.class);

	private Playlist _subject;

	@Before
	public void createTrackFiles() throws IOException {
		createTmpFiles("track1.mp3", "track2.mp3", "track3.mp3", "track4.mp3", "track5.mp3", "track6.wma");
	}

	@Test
	public void testSequentialPlaylist() {
		_subject = _playlistFactory.newSequentialPlaylist(tmpFolder());

		assertEquals("track1", _subject.nextTrack().name());
		assertEquals("track2", _subject.nextTrack().name());
		assertEquals("track3", _subject.nextTrack().name());
		assertEquals("track4", _subject.nextTrack().name());
		assertEquals("track5", _subject.nextTrack().name());

		assertEquals("track1", _subject.nextTrack().name());
	}

	@Test
	public void testRandomPlaylist() {
		_subject = _playlistFactory.newRandomPlaylist(tmpFolder());

		List<String> expected = new ArrayList<String>();
		expected.addAll(Arrays.asList("track1", "track2", "track3", "track4", "track5"));

		List<String> trackNamesReturned = new ArrayList<String>(expected.size());
		String trackNameReturned = null;
		// first random iteration through playlist's tracks
		while (expected.size() > 0) {
			trackNameReturned =_subject.nextTrack().name();
			trackNamesReturned.add(trackNameReturned);
			assertTrue(expected.contains(trackNameReturned));
			expected.remove(trackNameReturned);
		}
		assertTrue(expected.size() == 0);

		// second random iteration through playlist's tracks
		while (trackNamesReturned.size() > 0) {
			trackNameReturned = _subject.nextTrack().name();
			assertTrue(trackNamesReturned.contains(trackNameReturned));
			trackNamesReturned.remove(trackNameReturned);
		}
		assertTrue(trackNamesReturned.size() == 0);
	}

}

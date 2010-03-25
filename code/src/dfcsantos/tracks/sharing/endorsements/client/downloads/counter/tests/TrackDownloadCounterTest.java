package dfcsantos.tracks.sharing.endorsements.client.downloads.counter.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import dfcsantos.tracks.Tracks;
import dfcsantos.tracks.sharing.endorsements.client.downloads.counter.TrackDownloadCounter;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

public class TrackDownloadCounterTest extends BrickTest {

	private TrackDownloadCounter _subject;
	private final List<File> _peerTracks = new ArrayList<File>();

	@Bind private final Tracks _tracks = mock(Tracks.class);

	@Test (timeout = 1000)
	public void trackDownloadCount() {
		checking(new Expectations() {{
			exactly(3).of(_tracks).listMp3FilesFromFolder(with(peerTracksFolder())); will(returnValue(_peerTracks));
		}});

		setPeerTracks(5);
		_subject = my(TrackDownloadCounter.class);
		assertNumberOfDownloadedTracksEquals(5);

		_subject.decrement();
		assertNumberOfDownloadedTracksEquals(4);

		_subject.increment();
		assertNumberOfDownloadedTracksEquals(5);

		setPeerTracks(0);
		assertNumberOfDownloadedTracksEquals(5);
		my(Clock.class).advanceTime(15 * 60 * 1000);
		assertNumberOfDownloadedTracksEquals(0);

		setPeerTracks(1);
		assertNumberOfDownloadedTracksEquals(0);
		my(Clock.class).advanceTime(15 * 60 * 1000);
		assertNumberOfDownloadedTracksEquals(1);
	}

	private void assertNumberOfDownloadedTracksEquals(int expected) {
		my(SignalUtils.class).waitForValue(_subject.count(), expected);
	}

	private static File peerTracksFolder() {
		return my(TracksFolderKeeper.class).peerTracksFolder();	
	}

	private void setPeerTracks(int numberOfTracks) {
		_peerTracks.clear();
		for (int i = 0; i < numberOfTracks; ++i)
			_peerTracks.add(new File("track" + i + ".mp3"));
	}

}

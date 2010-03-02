package dfcsantos.wusic.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ClosureX;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.Wusic.OperatingMode;

public class WusicTest extends BrickTest {

	private Wusic _subject1;
	private Wusic _subject2;

//	@Bind private TrackPlayer _trackPlayer = mock(TrackPlayer.class);

	@Test
	public void basicStuff() {
		_subject1 = my(Wusic.class);

		File localSharedFolder = my(TracksFolderKeeper.class).sharedTracksFolder().currentValue();
		assertEquals(localSharedFolder, new File(tmpFolder(), "data/media/tracks"));
		
		assertSignalValue(_subject1.operatingMode(), OperatingMode.OWN);
		_subject1.switchOperatingMode();
		assertSignalValue(_subject1.operatingMode(), OperatingMode.PEERS);
		
		_subject1.start();
		if (!my(BlinkingLights.class).lights().currentGet(0).caption().equals("No Tracks Found"))
			fail();
		assertSignalValue(_subject1.playingTrack(), null);
		assertSignalValue(_subject1.isPlaying(), false);
		
		assertSignalValue(_subject1.numberOfPeerTracks(), 0);
		assertSignalValue(_subject1.isTrackDownloadActive(), false);
		
		_subject1.trackDownloadActivator().consume(true);
		
		assertSignalValue(_subject1.trackDownloadAllowance(), Wusic.DEFAULT_TRACKS_DOWNLOAD_ALLOWANCE);
	}

	@Ignore
	@Test
	public void ownMode() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet();
	}

	@Test
	public void peersMode() throws IOException {
		Environment remoteEnvironment = newTestEnvironment(my(TupleSpace.class));
		configureStorageFolder(remoteEnvironment, "remote/data");

		Environments.runWith(remoteEnvironment, new ClosureX<IOException>() { @Override public void run() throws IOException {
			createSampleTracks();
			_subject2 = my(Wusic.class);
			_subject2.trackDownloadActivator().consume(true);
		}});

		/* Implement: Check if tracks were transfered from one wusic to the other
		 * 		- Check number of downloaded tracks: _subject1.numberOfPeerTracks()
		 * 		- Play downloaded tracks
		 * 			- Keep one track and delete others
		 * 				- Check number of tracks in shared tracks after changes 
		*/

		crash(remoteEnvironment);
	}

	private <T> void assertSignalValue(Signal<T> signal, T value) {
		my(SignalUtils.class).waitForValue(signal, value);
	}

	private void createSampleTracks() throws IOException {
		String[] tracks = { "track1.mp3", "track2.mp3", "track3.mp3" };
		createTmpFilesWithFileNameAsContent(tracks);
		File remoteSharedFolder = my(TracksFolderKeeper.class).sharedTracksFolder().currentValue();
		for (String track : tracks)
			my(IO.class).files().copyFileToFolder(new File(tmpFolder(), track), remoteSharedFolder);
	}

}

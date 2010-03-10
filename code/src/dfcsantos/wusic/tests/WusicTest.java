package dfcsantos.wusic.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.server.FileServer;
import sneer.bricks.hardware.clock.ticker.custom.CustomClockTicker;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ClosureX;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.Tracks;
import dfcsantos.tracks.execution.player.TrackPlayer;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.Wusic.OperatingMode;

public class WusicTest extends BrickTest {

	private Wusic _subject1;
	private Wusic _subject2;

	@Bind private TrackPlayer _trackPlayer = mock(TrackPlayer.class);

	@Test
	public void basicStuff() {
		_subject1 = my(Wusic.class);

		File defaultFolder = new File(tmpFolder(), "data/media/tracks");
		assertEquals(_subject1.playingFolder(), defaultFolder);
		assertEquals(_subject1.sharedTracksFolder().currentValue(), defaultFolder);

		assertEquals(_subject1.operatingMode().currentValue(), OperatingMode.OWN);
		_subject1.setOperatingMode(OperatingMode.PEERS);
		waitForSignalValue(_subject1.operatingMode(), OperatingMode.PEERS);

		_subject1.start();
		if (!my(BlinkingLights.class).lights().currentGet(0).caption().equals("No Tracks Found")) fail();
		assertEquals(_subject1.playingTrack().currentValue(), null);
		assertEquals(_subject1.isPlaying().currentValue(), false);

		assertTrue(_subject1.numberOfPeerTracks().currentValue() == 0);
		assertEquals(_subject1.isTrackDownloadActive().currentValue(), false);

		_subject1.trackDownloadActivator().consume(true);
		waitForSignalValue(_subject1.isTrackDownloadActive(), true);		

		assertTrue(_subject1.trackDownloadAllowance().currentValue().equals(Wusic.DEFAULT_TRACKS_DOWNLOAD_ALLOWANCE));
	}

	@Test
	public void ownModeWithOneTrack() throws IOException {
		_subject1 = my(Wusic.class);
		createSampleTracks(_subject1.playingFolder(), "track1.mp3");

		checking(new Expectations() {{
			exactly(4).of(_trackPlayer).startPlaying(with(any(Track.class)), with(any(Signal.class)), with(any(Runnable.class)));
		}});

		_subject1.start(); // Starts 1st TrackContract
		waitForSignalValue(_subject1.isPlaying(), true);
		assertEquals("track1", playingTrack());

		_subject1.skip(); // Starts 2nd TrackContract
		waitForSignalValue(_subject1.isPlaying(), true);
		assertEquals("track1", playingTrack());

		_subject1.skip(); // Starts 3rd TrackContract
		waitForSignalValue(_subject1.isPlaying(), true);
		assertEquals("track1", playingTrack());

		_subject1.stop();
		waitForSignalValue(_subject1.isPlaying(), false);

		_subject1.pauseResume(); // Starts 4th TrackContract
		waitForSignalValue(_subject1.isPlaying(), true);
		assertEquals("track1", playingTrack());

		_subject1.pauseResume();
		waitForSignalValue(_subject1.isPlaying(), false);

		_subject1.pauseResume();
		waitForSignalValue(_subject1.isPlaying(), true);
		assertEquals("track1", playingTrack());

		_subject1.deleteTrack();
		waitForSignalValue(_subject1.isPlaying(), false);

		_subject1.pauseResume();
		if (!my(BlinkingLights.class).lights().currentGet(0).caption().equals("No Tracks Found")) fail();		
	}

	@Test (timeout = 2000)
	public void ownModeWithMultipleTracks() throws IOException {
		/*	Folder structure created:
		 * 
		 *	tmpFolder()
		 * 		|_ tmp/media/tracks (Default Playing Folder)
		 * 				|_ subdirectory1
		 * 						|_ track1.mp3
		 * 						|_ track2.mp3
		 * 				|_ subdirectory2
		 * 						|_ track3.mp3
		 * 						|_ track4.mp3
		 * 				|_ track5.mp3
		 * 				|_ track6.mp3
		 */

		_subject1 = my(Wusic.class);

		File rootFolder = _subject1.playingFolder();
		createSampleTracks(rootFolder, "track5.mp3", "track6.mp3");

		File subdirectory1 = new File(rootFolder, "subdirectory1");
		createSampleTracks(subdirectory1, "track1.mp3", "track2.mp3");

		File subdirectory2 = new File(rootFolder, "subdirectory2");
		createSampleTracks(subdirectory2, "track3.mp3", "track4.mp3");

		checking(new Expectations() {{
			allowing(_trackPlayer).startPlaying(with(any(Track.class)), with(any(Signal.class)), with(any(Runnable.class)));
		}});

		// Play all songs sequentially
		_subject1.start();
		assertEquals("track1", playingTrack());
		_subject1.skip();
		assertEquals("track2", playingTrack());
		_subject1.skip();
		assertEquals("track3", playingTrack());
		_subject1.skip();
		assertEquals("track4", playingTrack());
		_subject1.skip();
		assertEquals("track5", playingTrack());
		_subject1.skip();
		assertEquals("track6", playingTrack());
		_subject1.skip(); // Back to first track
		assertEquals("track1", playingTrack());

		// Play only the songs from subdirectory1
		_subject1.setPlayingFolder(subdirectory1);
		assertEquals(subdirectory1, _subject1.playingFolder());
		assertEquals("track1", playingTrack());
		_subject1.skip();
		assertEquals("track2", playingTrack());
		_subject1.skip();
		assertEquals("track1", playingTrack());

		_subject1.setPlayingFolder(rootFolder);
		_subject1.setShuffle(true);

		Track previousTrack = null;
		do {
			previousTrack = _subject1.playingTrack().currentValue();
			_subject1.skip();
		} while(areSequential(previousTrack, _subject1.playingTrack().currentValue()));

	}

	@Test (timeout = 4000)
	public void peersMode() throws IOException {
		my(CustomClockTicker.class).start(10, 2000);
		_subject1 = my(Wusic.class);

		Environment remoteEnvironment = newTestEnvironment(my(TupleSpace.class));
		downloadTracksFrom(remoteEnvironment);

		checking(new Expectations() {{
			exactly(3).of(_trackPlayer).startPlaying(with(any(Track.class)), with(any(Signal.class)), with(any(Runnable.class)));
		}});

		_subject1.setOperatingMode(OperatingMode.PEERS);

		// Deletes the first played track
		_subject1.start();
		Track deletedTrack = _subject1.playingTrack().currentValue();
		_subject1.deleteTrack(); // Skip is called automatically after a track is deleted
		assertNull(my(FileMap.class).getHash(deletedTrack.file()));

		// Keeps the last two played tracks
		List<String> keptTracks = new ArrayList<String>();
		keptTracks.add(_subject1.playingTrack().currentValue().name());
		_subject1.meToo();
		assertTrue(_subject1.isPlaying().currentValue()); // MeToo doesn't affect the playing track's flow
		_subject1.skip();
		keptTracks.add(_subject1.playingTrack().currentValue().name());
		_subject1.meToo();

		_subject1.stop();

		waitForSignalValue(_subject1.numberOfPeerTracks(), 0);

		List<Track> sharedTracks = my(Tracks.class).listTracksFromFolder(sharedTracksFolder()); 
		for (Track sharedTrack : sharedTracks) {
			assertTrue(keptTracks.contains(sharedTrack.name()));
			assertNotNull(my(FileMap.class).getHash(sharedTrack.file()));
		}

		crash(remoteEnvironment);
	}

	private <T> void waitForSignalValue(Signal<T> signal, T value) {
		my(SignalUtils.class).waitForValue(signal, value);
	}

	private void createSampleTracks(File tracksFolder, String... tracks) throws IOException {
		for (String track : tracks)
			my(IO.class).files().writeString(new File(tracksFolder, track), track);
	}

	private String playingTrack() {
		return _subject1.playingTrack().currentValue().name();
	}

	private boolean areSequential(Track previousTrack, Track currentTrack) {
		return lastCharOf(currentTrack.name()) - lastCharOf(previousTrack.name()) == 1;
	}

	private char lastCharOf(String string) {
		return string.charAt(string.length() - 1);
	}

	private void downloadTracksFrom(Environment remoteEnvironment) throws IOException {
		configureStorageFolder(remoteEnvironment, "remote/data");
		configureTmpFolder(remoteEnvironment, "remote/tmp");

		Environments.runWith(remoteEnvironment, new ClosureX<IOException>() { @Override public void run() throws IOException {
			my(CustomClockTicker.class).start(10, 2000);

			createSampleTracks(sharedTracksFolder(), new String[] { "track1.mp3", "track2.mp3", "track3.mp3" });
			assertEquals(3, sharedTracksFolder().listFiles().length);

			my(FileServer.class);

			_subject2 = my(Wusic.class);
			_subject2.trackDownloadActivator().consume(true);
		}});

		_subject1.trackDownloadActivator().consume(true);

		waitForSignalValue(_subject1.numberOfPeerTracks(), 3);
	}

	private File sharedTracksFolder() {
		return my(Wusic.class).sharedTracksFolder().currentValue();
	}

}

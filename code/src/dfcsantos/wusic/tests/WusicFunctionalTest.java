package dfcsantos.wusic.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.jmock.Expectations;
import org.junit.Ignore;
import org.junit.Test;

import scala.actors.threadpool.Arrays;
import sneer.bricks.hardware.clock.ticker.custom.CustomClockTicker;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.ram.collections.CollectionUtils;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ClosureX;
import sneer.foundation.lang.Functor;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.execution.player.TrackPlayer;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.Wusic.OperatingMode;

public class WusicFunctionalTest extends BrickTest {

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
		assertEquals(_subject1.playingTrack().currentValue(), null);
		assertEquals(_subject1.isPlaying().currentValue(), false);

		assertTrue(_subject1.numberOfOwnTracks().currentValue() == 0);
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
		if (!my(BlinkingLights.class).lights().currentGet(0).caption().equals("No Tracks to Play")) fail();		
	}

	@Test (timeout = 2000)
	public void ownModeWithMultipleTracks() throws IOException {
		/*	Folder structure created:
		 *
		 *	tmpFolder()
		 * 		|_ tmp/media/tracks (Default Playing Folder)
		 * 				|_ music.mp3
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
		_subject1.skip(); // Playlist reloaded
		assertEquals("track1", playingTrack());

		// Play only the songs from subdirectory1
		_subject1.setPlayingFolder(subdirectory1);
		assertEquals(subdirectory1, _subject1.playingFolder());
		assertEquals("track1", playingTrack());
		_subject1.skip();
		assertEquals("track2", playingTrack());
		_subject1.skip();
		assertEquals("track1", playingTrack());

		// Play all songs randomly
		_subject1.setPlayingFolder(rootFolder);
		_subject1.setShuffle(true);

		// Pseudo-random sequence (done by regression)
		_subject1.skip();
		assertEquals("track5", playingTrack());
		_subject1.skip();
		assertEquals("track2", playingTrack());
		_subject1.skip();
		assertEquals("track3", playingTrack());
		_subject1.skip();
		assertEquals("track6", playingTrack());
		_subject1.skip();
		assertEquals("track4", playingTrack());
		_subject1.skip();
		assertEquals("track1", playingTrack());
		_subject1.skip(); // Playlist reloaded
		assertEquals("track5", playingTrack());
	}

	@Ignore
	@Test (timeout = 4000)
	public void peersMode() throws IOException {
		Environment remoteEnvironment = newTestEnvironment(my(TupleSpace.class));
		configureFoldersOf(remoteEnvironment);

		activateTrackEndorsementsFrom(remoteEnvironment);

		_subject1 = my(Wusic.class);
		_subject1.trackDownloadActivator().consume(true);

		my(CustomClockTicker.class).start(10, 2000);

		waitForSignalValue(_subject1.numberOfPeerTracks(), 3);

		checking(new Expectations() {{
			exactly(3).of(_trackPlayer).startPlaying(with(any(Track.class)), with(any(Signal.class)), with(any(Runnable.class)));
		}});

		_subject1.setOperatingMode(OperatingMode.PEERS);

		// Deletes first played track
		_subject1.start();
		_subject1.deleteTrack(); // Skip is called automatically after a track is deleted

		File[] keptTracks = new File[2];
		keptTracks[0] = _subject1.playingTrack().currentValue().file();
		_subject1.meToo(); // Keeps second played track
		assertTrue(_subject1.isPlaying().currentValue()); // MeToo doesn't affect the playing track's flow

		_subject1.skip();
		keptTracks[1] = _subject1.playingTrack().currentValue().file();
		_subject1.meToo(); // Keeps last played track

		waitForSignalValue(_subject1.numberOfPeerTracks(), 0);

		File[] sharedTracks = sharedTracksFolder().listFiles();
		assertEquals(2, sharedTracks.length);
		assertElementsInAnyOrder(trackNames(keptTracks), trackNames(sharedTracks).toArray(new String[0]));

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

	private void configureFoldersOf(Environment remoteEnvironment) {
		configureStorageFolder(remoteEnvironment, "remote/data");
		configureTmpFolder(remoteEnvironment, "remote/tmp");
	}

	private void activateTrackEndorsementsFrom(Environment remoteEnvironment) throws IOException {
		Environments.runWith(remoteEnvironment, new ClosureX<IOException>() { @Override public void run() throws IOException {
			createSampleTracks(sharedTracksFolder(), new String[] { "track1.mp3", "track2.mp3", "track3.mp3" });
			assertEquals(3, sharedTracksFolder().listFiles().length);

			_subject2 = my(Wusic.class);
			_subject2.trackDownloadActivator().consume(true);

			my(CustomClockTicker.class).start(10, 2000);
		}});
	}

	private Collection<String> trackNames(File[] trackFiles) {
		return my(CollectionUtils.class).map(
			Arrays.asList(trackFiles),
			new Functor<File, String>() { @Override public String evaluate(File trackFile) throws RuntimeException {
				return trackFile.getName();
			}}
		);
	}

	private File sharedTracksFolder() {
		return my(Wusic.class).sharedTracksFolder().currentValue();
	}

}

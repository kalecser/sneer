package dfcsantos.wusic.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import sneer.bricks.expression.files.server.FileServer;
import sneer.bricks.hardware.clock.ticker.custom.CustomClockTicker;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ClosureX;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.Wusic.OperatingMode;

public class WusicTest extends BrickTest {

	private Wusic _subject1;
	private Wusic _subject2;

//	@Bind private TrackPlayer _trackPlayer = mock(TrackPlayer.class);

	@Test
	public void basicStuff() {
		_subject1 = my(Wusic.class);

		waitForSignalValue(_subject1.sharedTracksFolder(), new File(tmpFolder(), "data/media/tracks"));

		waitForSignalValue(_subject1.operatingMode(), OperatingMode.OWN);
		_subject1.setOperatingMode(OperatingMode.PEERS);
		waitForSignalValue(_subject1.operatingMode(), OperatingMode.PEERS);

		_subject1.start();
		if (!my(BlinkingLights.class).lights().currentGet(0).caption().equals("No Tracks Found")) fail();
		waitForSignalValue(_subject1.playingTrack(), null);
		waitForSignalValue(_subject1.isPlaying(), false);

		waitForSignalValue(_subject1.numberOfPeerTracks(), 0);
		waitForSignalValue(_subject1.isTrackDownloadActive(), false);

		_subject1.trackDownloadActivator().consume(true);

		waitForSignalValue(_subject1.isTrackDownloadActive(), true);		
		waitForSignalValue(_subject1.trackDownloadAllowance(), Wusic.DEFAULT_TRACKS_DOWNLOAD_ALLOWANCE);
	}

	@Test (timeout = 6000)
	public void peersMode() throws IOException {
		my(CustomClockTicker.class).start(10, 2000);

		Environment remoteEnvironment = newTestEnvironment(my(TupleSpace.class));
		configureStorageFolder(remoteEnvironment, "remote/data");
		configureTmpFolder(remoteEnvironment, "remote/tmp");

		final String[] trackNames = new String[] { "track1.mp3", "track2.mp3", "track3.mp3" };
		Environments.runWith(remoteEnvironment, new ClosureX<IOException>() { @Override public void run() throws IOException {
			my(CustomClockTicker.class).start(10, 2000);

			createSampleTracks(trackNames);
			assertEquals(3, sharedTracksFolder().listFiles().length);

			my(FileServer.class);

			_subject2 = my(Wusic.class);
			_subject2.trackDownloadActivator().consume(true);
		}});

		_subject1 = my(Wusic.class);
		_subject1.trackDownloadActivator().consume(true);

		waitForSignalValue(_subject1.numberOfPeerTracks(), 3);

//		checking(new Expectations() {{
//			oneOf(_trackPlayer).startPlaying(with(any(Track.class)), with(any(Signal.class)), with(any(Runnable.class)));
//		}});
//
//		_subject1.setOperatingMode(OperatingMode.PEERS);
//		_subject1.start();
//		waitForSignalValue(_subject1.isPlaying(), true);
//		_subject1.pauseResume();
//		waitForSignalValue(_subject1.isPlaying(), false);

		crash(remoteEnvironment);
	}

	private <T> void waitForSignalValue(Signal<T> signal, T value) {
		my(SignalUtils.class).waitForValue(signal, value);
	}

	private void createSampleTracks(String... tracks) throws IOException {
		for (String track : tracks)
			my(IO.class).files().writeString(new File(sharedTracksFolder(), track), track);
	}

	private File sharedTracksFolder() {
		return my(Wusic.class).sharedTracksFolder().currentValue();
	}

}

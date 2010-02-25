package dfcsantos.wusic.notification.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.ClosureX;
import sneer.foundation.lang.exceptions.Refusal;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.Tracks;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.notification.client.PlayingTrackClient;
import dfcsantos.wusic.notification.keeper.PlayingTrackKeeper;
import dfcsantos.wusic.notification.server.PlayingTrackServer;

public class PlayingTrackTest extends BrickTest {

	@Bind private final Wusic _wusic = mock(Wusic.class);
	private final Register<Track> _playingTrack = my(Signals.class).newRegister(null);

	private Contact _localContact;
	private PlayingTrackKeeper _remoteKeeper;

	@Test
	public void playingTrackBroadcast() throws Exception {
		checking(new Expectations() {{
			oneOf(_wusic).playingTrack(); will(returnValue(_playingTrack.output()));
		}});

		my(PlayingTrackServer.class);

		Environment remote = newTestEnvironment(my(TupleSpace.class), my(Clock.class));
		configureStorageFolder(remote);

		final Seal localSeal = my(OwnSeal.class).get();
		Environments.runWith(remote, new ClosureX<Refusal>() { @Override public void run() throws Refusal {
			_localContact = my(Contacts.class).produceContact("local");
			my(ContactSeals.class).put("local", localSeal);
			_remoteKeeper = my(PlayingTrackKeeper.class);
			my(PlayingTrackClient.class);
		}});

		testPlayingTrack("track1");
		testPlayingTrack("track2");
		testPlayingTrack("track2");
		testPlayingTrack("track3");
		testPlayingTrack("");
		testPlayingTrack("track4");

		testNullPlayingTrack();

		crash(remote);
	}

	private void testPlayingTrack(String trackName) {
		setPlayingTrack(trackName.isEmpty() ? "" : trackName + ".mp3");
		my(TupleSpace.class).waitForAllDispatchingToFinish();
		assertEquals(trackName, playingTrackReceivedFromLocal());
	}

	private void testNullPlayingTrack() {
		my(Clock.class).advanceTime(1);
		_playingTrack.setter().consume(null);
		my(TupleSpace.class).waitForAllDispatchingToFinish();
		assertEquals("", playingTrackReceivedFromLocal());
	}

	private String playingTrackReceivedFromLocal() {
		return _remoteKeeper.playingTrack(_localContact).currentValue();
	}

	private void setPlayingTrack(String trackName) {
		_playingTrack.setter().consume(my(Tracks.class).newTrack(new File(trackName)));
	}

	private void configureStorageFolder(Environment remote) {
		Environments.runWith(remote, new Closure() { @Override public void run() {
			my(FolderConfig.class).storageFolder().set(newTmpFile("remote"));
		}});
	}

	private void crash(Environment remote) {
		Environments.runWith(remote, new Closure() { @Override public void run() {
			my(Threads.class).crashAllThreads();
		}});
	}

}

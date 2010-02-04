package dfcsantos.tracks.sharing.playingtracks.tests;

import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.sharing.playingtracks.client.PlayingTrackClient;
import dfcsantos.tracks.sharing.playingtracks.keeper.PlayingTrackKeeper;
import dfcsantos.tracks.sharing.playingtracks.server.PlayingTrackServer;
import dfcsantos.wusic.Wusic;

public class PlayingTrackTest extends BrickTest {

	@Bind private final Wusic _wusic = mock(Wusic.class);
	private final Register<String> _playingTrack = my(Signals.class).newRegister("");

	private Contact _localContact;
	private PlayingTrackKeeper _remoteKeeper;

	@Test
	public void playingTrackExchange() {
		checking(new Expectations() {{
			oneOf(_wusic).playingTrackName(); will(returnValue(_playingTrack.output()));
		}});

		my(PlayingTrackServer.class);

		Environment remote = newTestEnvironment(my(TupleSpace.class), my(Clock.class));
		configureStorageFolder(remote);

		final Seal localSeal = my(Seals.class).ownSeal();
		Environments.runWith(remote, new Closure() { @Override public void run() {
			_localContact = my(Contacts.class).produceContact("local");
			my(Seals.class).put("local", localSeal);
			_remoteKeeper = my(PlayingTrackKeeper.class);
			my(PlayingTrackClient.class);
		}});

		_remoteKeeper.playingTrack(_localContact).addReceiver(new Consumer<String>() { @Override public void consume(String remotePlayingTrack) {
			assertEquals(playingTrack(), remotePlayingTrack);
		}});

		setPlayingTrack("track1.mp3");
		setPlayingTrack("track2.mp3");
		setPlayingTrack("track2.mp3");
		setPlayingTrack("track3.mp3");
		setPlayingTrack("");

		crash(remote);
	}

	private void setPlayingTrack(String trackName) {
		_playingTrack.setter().consume(trackName);
	}

	String playingTrack() {
		return _playingTrack.output().currentValue();
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

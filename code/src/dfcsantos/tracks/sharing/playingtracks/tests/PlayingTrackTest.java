package dfcsantos.tracks.sharing.playingtracks.tests;

import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import dfcsantos.tracks.sharing.playingtracks.client.PlayingTrackClient;
import dfcsantos.tracks.sharing.playingtracks.keeper.PlayingTrackKeeper;
import dfcsantos.tracks.sharing.playingtracks.server.PlayingTrackServer;
import dfcsantos.wusic.Wusic;

@Ignore
public class PlayingTrackTest extends BrickTest {

	private final Wusic _wusic = mock(Wusic.class);

	private final Register<String> _playingTrack = my(Signals.class).newRegister(null);

	@Test
	public void playingTrackBroadcast() {
		final Contact myself = my(Contacts.class).produceContact("myself");
		my(Seals.class).put("myself", my(Seals.class).ownSeal());

		checking(new Expectations() {{
			oneOf(_wusic).playingTrackName(); will(returnValue(_playingTrack.output()));
		}});

		my(PlayingTrackClient.class);
		my(PlayingTrackServer.class);

		assertEquals(null, my(PlayingTrackKeeper.class).getPlayingTrackOf(myself));

		setPlayingTrack("track1.mp3");
		assertEquals("track1", my(PlayingTrackKeeper.class).getPlayingTrackOf(myself));

		setPlayingTrack("track2.mp3");
		assertEquals("track2", my(PlayingTrackKeeper.class).getPlayingTrackOf(myself));

		setPlayingTrack("track2.mp3");
		assertEquals("track2", my(PlayingTrackKeeper.class).getPlayingTrackOf(myself));

		setPlayingTrack("track3.mp3");
		assertEquals("track3", my(PlayingTrackKeeper.class).getPlayingTrackOf(myself));

		setPlayingTrack(null);
		assertEquals("track3", my(PlayingTrackKeeper.class).getPlayingTrackOf(myself));
	}

	private void setPlayingTrack(String trackName) {
		_playingTrack.setter().consume(trackName);
	}

}

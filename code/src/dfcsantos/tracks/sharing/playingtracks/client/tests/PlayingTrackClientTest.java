package dfcsantos.tracks.sharing.playingtracks.client.tests;

import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.tuples.Tuple;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import dfcsantos.tracks.sharing.playingtracks.client.PlayingTrackClient;
import dfcsantos.tracks.sharing.playingtracks.keeper.PlayingTrackKeeper;
import dfcsantos.tracks.sharing.playingtracks.protocol.NullPlayingTrack;
import dfcsantos.tracks.sharing.playingtracks.protocol.PlayingTrack;

@Ignore
public class PlayingTrackClientTest extends BrickTest {

	private final Seals _seals = mock(Seals.class);

	@Test
	public void playingTrackAcknowledgement() {
		final Seal ownSeal = new Seal(new ImmutableByteArray(new byte[] { 66 }));
		final Seal whatsitsSeal = new Seal(new ImmutableByteArray(new byte[] { 42 }));
		final Contact whatsit = my(Contacts.class).produceContact("whatsit");
		my(Seals.class).put("whatsit", whatsitsSeal);

		checking(new Expectations() {{
			oneOf(_seals).ownSeal(); will(returnValue(whatsitsSeal));
			oneOf(_seals).ownSeal(); will(returnValue(ownSeal));

			oneOf(_seals).ownSeal(); will(returnValue(whatsitsSeal));
			oneOf(_seals).ownSeal(); will(returnValue(ownSeal));

			oneOf(_seals).ownSeal(); will(returnValue(whatsitsSeal));
			oneOf(_seals).ownSeal(); will(returnValue(ownSeal));

			oneOf(_seals).ownSeal(); will(returnValue(whatsitsSeal));
			oneOf(_seals).ownSeal(); will(returnValue(ownSeal));

			oneOf(_seals).ownSeal(); will(returnValue(whatsitsSeal));
			oneOf(_seals).ownSeal(); will(returnValue(ownSeal));
		}});

		my(PlayingTrackClient.class);

		assertEquals("", my(PlayingTrackKeeper.class).playingTrack(whatsit));

		sendPlayingTrack("track1.mp3");
		assertEquals("track1", my(PlayingTrackKeeper.class).playingTrack(whatsit));

		sendPlayingTrack("track2.mp3");
		assertEquals("track2", my(PlayingTrackKeeper.class).playingTrack(whatsit));

		sendPlayingTrack("track2.mp3");
		assertEquals("track2", my(PlayingTrackKeeper.class).playingTrack(whatsit));

		sendPlayingTrack("track3.mp3");
		assertEquals("track3", my(PlayingTrackKeeper.class).playingTrack(whatsit));

		sendPlayingTrack(null);
		assertEquals("", my(PlayingTrackKeeper.class).playingTrack(whatsit));
	}

	private void sendPlayingTrack(String trackName) {
		Tuple playingTrack = (trackName == null) ? new NullPlayingTrack() : new PlayingTrack(trackName);
		my(TupleSpace.class).acquire(playingTrack);
	}

}

package dfcsantos.tracks.sharing.playingtracks.tests;

import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.ticker.custom.CustomClockTicker;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import dfcsantos.tracks.sharing.playingtracks.client.PlayingTrackClient;
import dfcsantos.tracks.sharing.playingtracks.keeper.PlayingTrackKeeper;
import dfcsantos.tracks.sharing.playingtracks.server.PlayingTrackServer;
import dfcsantos.wusic.Wusic;

@Ignore // Draft yet to be finished
public class PlayingTrackTest extends BrickTest {

	private final Wusic _wusic = mock(Wusic.class);
	private final PlayingTrackKeeper _keeper = mock(PlayingTrackKeeper.class);

	private final Register<String> _playingTrack = my(Signals.class).newRegister("");

	@Test
	public void playingTrackExchange() {
		final Seal whatsitsSeal = new Seal(new ImmutableByteArray(new byte[] { 42 }));
		final Contact whatsit = my(Contacts.class).produceContact("whatsit");
		my(Seals.class).put("whatsit", whatsitsSeal);

		checking(new Expectations() {{
			oneOf(_wusic).playingTrackName(); will(returnValue(_playingTrack.output()));
			oneOf(_keeper).playingTrack(whatsit);
			oneOf(_keeper).playingTrack(whatsit);
			oneOf(_keeper).playingTrack(whatsit);
			oneOf(_keeper).playingTrack(whatsit);
			oneOf(_keeper).playingTrack(whatsit);
		}});

		@SuppressWarnings("unused") PlayingTrackClient client = my(PlayingTrackClient.class);
		my(CustomClockTicker.class).start(10, 15000);
		Environment remote = newTestEnvironment(my(TupleSpace.class), my(Clock.class));

		Environments.runWith(remote, new Closure() { @Override public void run() {
			@SuppressWarnings("unused") PlayingTrackServer server = my(PlayingTrackServer.class);
			
			setPlayingTrack("track1.mp3");
			setPlayingTrack("track2.mp3");
			setPlayingTrack("track2.mp3");
			setPlayingTrack("track3.mp3");
			setPlayingTrack(null);
		}});
	}

	private void setPlayingTrack(String trackName) {
		_playingTrack.setter().consume(trackName);
	}

}

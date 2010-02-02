package dfcsantos.tracks.sharing.playingtracks.server.tests;

import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import dfcsantos.tracks.sharing.playingtracks.protocol.NullPlayingTrack;
import dfcsantos.tracks.sharing.playingtracks.protocol.PlayingTrack;
import dfcsantos.tracks.sharing.playingtracks.server.PlayingTrackServer;
import dfcsantos.wusic.Wusic;

@Ignore
public class PlayingTrackServerTest extends BrickTest {

	private final Wusic _wusic = mock(Wusic.class);
	private final TupleSpace _tupleSpace = mock(TupleSpace.class);

	private final Register<String> _playingTrack = my(Signals.class).newRegister(null);

	@Test
	public void playingTrackBroadcast() {
		checking(new Expectations() {{
			oneOf(_wusic).playingTrackName(); will(returnValue(_playingTrack.output()));
			oneOf(_tupleSpace).acquire(with(new PlayingTrack("track1.mp3")));
			oneOf(_tupleSpace).acquire(with(new PlayingTrack("track2.mp3")));
			oneOf(_tupleSpace).acquire(with(new PlayingTrack("track3.mp3")));
			oneOf(_tupleSpace).acquire(with(new NullPlayingTrack()));
		}});

		@SuppressWarnings("unused")
		PlayingTrackServer server = my(PlayingTrackServer.class);

		setPlayingTrack("track1.mp3");
		setPlayingTrack("track2.mp3");
		setPlayingTrack("track2.mp3");
		setPlayingTrack("track3.mp3");
		setPlayingTrack(null);
	}

	private void setPlayingTrack(String trackName) {
		_playingTrack.setter().consume(trackName);
	}

}

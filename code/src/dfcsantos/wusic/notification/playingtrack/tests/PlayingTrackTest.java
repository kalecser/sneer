package dfcsantos.wusic.notification.playingtrack.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.Tracks;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.notification.playingtrack.PlayingTrack;
import dfcsantos.wusic.notification.playingtrack.server.PlayingTrackPublisher;

public class PlayingTrackTest extends BrickTest {

	@Bind private final Wusic _wusic = mock(Wusic.class);
	@Bind private final Attributes _attributes = mock(Attributes.class);

	private final Register<Track> _playingTrack = my(Signals.class).newRegister(null);

	@Test
	public void playingTrackBroadcast() throws Exception {
		checking(new Expectations() {{
			oneOf(_wusic).playingTrack(); will(returnValue(_playingTrack.output())); 
			exactly(5).of(_attributes).myAttributeSetter(PlayingTrack.class); // It's 5 times because the register initialization also counts
		}});

		my(PlayingTrackPublisher.class);

		setPlayingTrack("track1");
		setPlayingTrack("track2");
		setPlayingTrack(null);
		setPlayingTrack("track3");
	}

	private void setPlayingTrack(String trackName) {
		Track track = (trackName == null) ? null : newTrack(trackName);
		_playingTrack.setter().consume(track);
	}

	private Track newTrack(final String name) {
		return my(Tracks.class).newTrack(new File(name + ".mp3"));
	}

}

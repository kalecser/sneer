package dfcsantos.music.notification.playingtrack.server.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.foundation.lang.Consumer;
import dfcsantos.music.Wusic;
import dfcsantos.music.notification.playingtrack.PlayingTrack;
import dfcsantos.music.notification.playingtrack.server.PlayingTrackPublisher;
import dfcsantos.tracks.Track;

class PlayingTrackPublisherImpl implements PlayingTrackPublisher {

	@SuppressWarnings("unused") private final WeakContract _refToAvoidGC;

	{
		_refToAvoidGC = my(Wusic.class).playingTrack().addReceiver(new Consumer<Track>() { @Override public void consume(Track playingTrack) {
			publishPlayingTrack(playingTrack);
		}});
	}

	private void publishPlayingTrack(Track playingTrack) {
		String value = (playingTrack == null) ? null : playingTrack.name();
		my(Attributes.class).myAttributeSetter(PlayingTrack.class).consume(value);
	}

}

package dfcsantos.wusic.notification.playingtrack.server.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.Track;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.notification.playingtrack.PlayingTrack;
import dfcsantos.wusic.notification.playingtrack.server.PlayingTrackPublisher;

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

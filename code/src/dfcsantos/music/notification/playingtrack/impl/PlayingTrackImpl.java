package dfcsantos.music.notification.playingtrack.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.network.social.attributes.Attributes;
import dfcsantos.music.notification.playingtrack.PlayingTrack;

class PlayingTrackImpl implements PlayingTrack {

	{
		my(Attributes.class).registerAttribute(PlayingTrack.class);
	}

}

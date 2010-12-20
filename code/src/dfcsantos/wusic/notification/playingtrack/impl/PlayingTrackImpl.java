package dfcsantos.wusic.notification.playingtrack.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.network.social.attributes.Attributes;
import dfcsantos.wusic.notification.playingtrack.PlayingTrack;

class PlayingTrackImpl implements PlayingTrack {

	{
		my(Attributes.class).registerAttribute(PlayingTrack.class);
	}

}

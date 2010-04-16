package dfcsantos.wusic.notification.keeper;

import sneer.bricks.network.social.contacts.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface PlayingTrackKeeper {

	Signal<String> playingTrack(Contact contact);

	void setPlayingTrack(Contact contact, String playingTrack);

}

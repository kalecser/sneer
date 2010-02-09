package dfcsantos.tracks.sharing.playingtracks.keeper;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick
public interface PlayingTrackKeeper {

	Signal<String> playingTrack(Contact contact);

	void setPlayingTrack(Contact contact, String playingTrack);

}

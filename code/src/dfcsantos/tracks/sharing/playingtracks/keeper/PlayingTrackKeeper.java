package dfcsantos.tracks.sharing.playingtracks.keeper;

import sneer.bricks.network.social.Contact;
import sneer.foundation.brickness.Brick;

@Brick
public interface PlayingTrackKeeper {

//	MapSignal<Contact, String> playingTracksByContact();

	String getPlayingTrackOf(Contact contact);

	void setPlayingTrackOf(Contact contact, String playingTrack);

}

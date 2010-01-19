package dfcsantos.tracks.sharing.playingtracks.keeper.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.network.social.Contact;
import dfcsantos.tracks.sharing.playingtracks.keeper.PlayingTrackKeeper;

class PlayingTrackKeeperImpl implements PlayingTrackKeeper {

//	private MapRegister<Contact, String> _playingTracksByContact = my(CollectionSignals.class).newMapRegister();

//	@Override
//	public MapSignal<Contact, String> playingTracksByContact() {
//		return _playingTracksByContact.output();
//	}

	private Map<Contact, String> _playingTracksByContact = new ConcurrentHashMap<Contact, String>();

	@Override
	synchronized
	public String getPlayingTrackOf(Contact contact) {
		return _playingTracksByContact.get(contact);
	}

	@Override
	synchronized
	public void setPlayingTrackOf(Contact contact, String playingTrack) {
		_playingTracksByContact.put(contact, playingTrack);
	}

}

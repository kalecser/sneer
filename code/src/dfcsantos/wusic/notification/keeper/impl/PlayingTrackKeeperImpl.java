package dfcsantos.wusic.notification.keeper.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;
import dfcsantos.wusic.notification.keeper.PlayingTrackKeeper;

class PlayingTrackKeeperImpl implements PlayingTrackKeeper {

	private CacheMap<Contact, Register<String>> _playingTracksByContact = CacheMap.newInstance();

	@Override
	public Signal<String> playingTrack(Contact contact) {
		return playingTrackRegister(contact).output();
	}

	@Override
	public void setPlayingTrack(Contact contact, String playingTrack) {
		playingTrackRegister(contact).setter().consume(playingTrack);
	}

	private Register<String> playingTrackRegister(Contact contact) throws RuntimeException {
		return _playingTracksByContact.get(contact, new Producer<Register<String>>() { @Override public Register<String> produce() throws RuntimeException {
			return my(Signals.class).newRegister("");
		}});
	}

}

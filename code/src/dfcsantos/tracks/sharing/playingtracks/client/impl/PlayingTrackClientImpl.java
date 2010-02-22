package dfcsantos.tracks.sharing.playingtracks.client.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.keymanager.ContactSeals;
import sneer.bricks.pulp.tuples.TupleSpace;
import dfcsantos.tracks.sharing.playingtracks.client.PlayingTrackClient;
import dfcsantos.tracks.sharing.playingtracks.keeper.PlayingTrackKeeper;
import dfcsantos.tracks.sharing.playingtracks.protocol.PlayingTrack;

class PlayingTrackClientImpl implements PlayingTrackClient {

	private static final PlayingTrackKeeper PlayingTrackKeeper = my(PlayingTrackKeeper.class);

	@SuppressWarnings("unused") private final WeakContract _refToAvoidGC;

	{
		_refToAvoidGC = my(TupleSpace.class).addSubscription(PlayingTrack.class, this);
	}

	@Override
	public void consume(PlayingTrack playingTrack) {
		if (my(ContactSeals.class).ownSeal().equals(playingTrack.publisher)) return;

		Contact contact = my(ContactSeals.class).contactGiven(playingTrack.publisher);
		if (contact == null) {
			my(Logger.class).log("PlayingTrack received from unkown contact: ", playingTrack.publisher);
			return;
		}

		PlayingTrackKeeper.setPlayingTrack(contact, playingTrack.name);
	}

}

package dfcsantos.tracks.sharing.playingtracks.client.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.keymanager.Seals;
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
		if (my(Seals.class).ownSeal().equals(playingTrack.publisher)) return;

		Contact contact = my(Seals.class).contactGiven(playingTrack.publisher);
		if (contact == null) doSomething();

		if (playingTrack.name.equals(PlayingTrackKeeper.getPlayingTrackOf(contact))) return;
		PlayingTrackKeeper.setPlayingTrackOf(contact, playingTrack.name);
	}

	private void doSomething() {
		/* Options:
		 * 	- Add the contact;
		 * 	- Return;
		 * 	- Throw an IllegalStateException();
		 * 	- Call the cops, but do something!!!
		 */

		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

}

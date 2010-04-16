package dfcsantos.tracks.tastematching.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.social.contacts.Contact;
import sneer.bricks.network.social.contacts.Contacts;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;
import dfcsantos.tracks.tastematching.MusicalTasteMatcher;

class MusicalTasteMatcherImpl implements MusicalTasteMatcher {

	private final CacheMap<Contact, PeerMatchCounter> _matchesByPeer = CacheMap.newInstance();

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;

	@Override
	public void processEndorsement(String nickname, String folder, boolean isKnownTrack) {
		Contact peer = my(Contacts.class).contactGiven(nickname);
		FolderMatchCounter counter = matchesBy(peer, folder);
		counter.incrementEndorsementCount();
		if (isKnownTrack) counter.incrementMatchCount();
	}

	@Override
	public float ratingFor(String nickname, String folder) {
		Contact peer = my(Contacts.class).contactGiven(nickname);
		return matchesBy(peer, folder).matchRating();
	}

	private FolderMatchCounter matchesBy(Contact peer, String folder) {
		return matchesBy(peer).matchesBy(folder);
	}

	private PeerMatchCounter matchesBy(Contact peer) {
		return _matchesByPeer.get(peer, new Producer<PeerMatchCounter>() { @Override public PeerMatchCounter produce() throws RuntimeException {
			return new PeerMatchCounter();
		}});
	}

}

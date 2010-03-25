package dfcsantos.tracks.tastematching.impl;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.social.Contact;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;
import dfcsantos.tracks.tastematching.MusicalTasteMatcher;

class MusicalTasteMatcherImpl implements MusicalTasteMatcher {

	private final CacheMap<Contact, PeerMatchCounter> _matchesByPeer = CacheMap.newInstance();

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;

	@Override
	public void processEndorsement(Contact sender, String folder, boolean isKnownTrack) {
		if (isKnownTrack)
			processEndorsementOfKnownTrack(sender, folder);
		else
			processEndorsementOfUnknownTrack(sender, folder);
	}

	@Override
	public float ratingFor(Contact sender, String folder) {
		return matchesBy(sender, folder).matchRating();
	}

	private void processEndorsementOfKnownTrack(Contact peer, String folder) {
		FolderMatchCounter counter = matchesBy(peer, folder); 
		counter.incrementEndorsementCount();
		counter.incrementMatchCount();
	}

	private void processEndorsementOfUnknownTrack(Contact peer, String folder) {
		matchesBy(peer, folder).incrementEndorsementCount();
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

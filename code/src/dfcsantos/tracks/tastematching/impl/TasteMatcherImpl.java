package dfcsantos.tracks.tastematching.impl;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.social.Contact;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;
import dfcsantos.tracks.tastematching.TasteMatcher;

class TasteMatcherImpl implements TasteMatcher {

	private final CacheMap<Contact, PeerMatchCounter> countersByPeer = CacheMap.newInstance();

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;

	@Override
	public float rateEndorsement(Contact sender, String folder, Boolean opinion) {
		FolderMatchCounter counter = matchCounterFor(sender, folder);
		counter.countOpinion(opinion);
		return counter.matchRating();
	}

	private FolderMatchCounter matchCounterFor(Contact peer, String folder) {
		return matchesCounterFor(peer).matchCounterFor(folder);
	}

	private PeerMatchCounter matchesCounterFor(Contact peer) {
		return countersByPeer.get(peer, new Producer<PeerMatchCounter>() { @Override public PeerMatchCounter produce() throws RuntimeException {
			return new PeerMatchCounter();
		}});
	}

}

package dfcsantos.tracks.tastematching.impl;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.network.social.Contact;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;
import dfcsantos.tracks.tastematching.MusicalTasteMatcher;

class MusicalTasteMatcherImpl implements MusicalTasteMatcher {

	private final CacheMap<Contact, CacheMap<String, MatchCounter>> _matchesByPeer = CacheMap.newInstance();

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;

	@Override
	public void processEndorsementOfKnownTrack(Contact sender, String folder) {
		MatchCounter counter = matchesBy(sender, folder); 
		counter._endorsementCount++;
		counter._matchCount++;
	}

	@Override
	public float processEndorsementOfUnknownTrackAndReturnMatchRating(Contact sender, String folder) {
		MatchCounter counter = matchesBy(sender, folder); 
		counter._endorsementCount++;
		return matchRating(counter);
	}

	private MatchCounter matchesBy(Contact peer, String folder) {
		return matchesBy(peer).get(folder, new Producer<MatchCounter>() { @Override public MatchCounter produce() throws RuntimeException {
			return new MatchCounter();
		}});
	}

	private CacheMap<String, MatchCounter> matchesBy(Contact peer) {
		return _matchesByPeer.get(peer, new Producer<CacheMap<String,MatchCounter>>() { @Override public CacheMap<String, MatchCounter> produce() throws RuntimeException {
			return CacheMap.newInstance();
		}});
	}

	private float matchRating(MatchCounter counter) {
		return counter._matchCount / counter._endorsementCount;
	}

}

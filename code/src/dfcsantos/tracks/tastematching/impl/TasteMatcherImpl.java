package dfcsantos.tracks.tastematching.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.network.social.Contact;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;
import dfcsantos.tracks.tastematching.TasteMatcher;


class TasteMatcherImpl implements TasteMatcher {

	private final CacheMap<Contact, FolderMatchCounter> countersByPeer = CacheMap.newInstance();

	
	@Override
	public float rateEndorsement(Contact sender, String folder, Boolean opinion) {
		float rating = matchCounterFor(sender).rate(folder, opinion);
		my(Logger.class).log("Musical taste match for folder {}:", folder, rating);
		return rating;
	}


	private FolderMatchCounter matchCounterFor(Contact peer) {
		return countersByPeer.get(peer, new Producer<FolderMatchCounter>() { @Override public FolderMatchCounter produce() throws RuntimeException {
			return new FolderMatchCounter();
		}});
	}

}

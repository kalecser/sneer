package dfcsantos.tracks.tastematching.impl;

import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;

class PeerMatchCounter {

	private CacheMap<String, FolderMatchCounter> _matchesByFolder = CacheMap.newInstance(); 

	FolderMatchCounter matchesBy(String folder) {
		return _matchesByFolder.get(folder, new Producer<FolderMatchCounter>() { @Override public FolderMatchCounter produce() throws RuntimeException {
			return new FolderMatchCounter();
		}});
	}

}

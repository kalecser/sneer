package dfcsantos.tracks.tastematching.impl;

import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;

class PeerMatchCounter {

	private CacheMap<String, FolderMatchCounter> countersByFolder = CacheMap.newInstance(); 

	FolderMatchCounter matchCounterFor(String folder) {
		return countersByFolder.get(folder, new Producer<FolderMatchCounter>() { @Override public FolderMatchCounter produce() throws RuntimeException {
			return new FolderMatchCounter();
		}});
	}

}

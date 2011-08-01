package dfcsantos.tracks.tastematching.impl;

import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;

class FolderMatchCounter {

	private CacheMap<String, MatchCounter> countersByFolder = CacheMap.newInstance(); 


	float rate(String folder, Boolean opinion) {
		MatchCounter counter = matchCounterFor(folder);
		counter.countOpinion(opinion);
		
		while (true) {
			float rating = new Rating(countersByFolder, folder).result();
			if (folder.isEmpty()) return rating;
			if (rating != 0) return rating;
			folder = parent(folder);
		}
	}

	
	private String parent(String folder) {
		int index = folder.lastIndexOf("/");
		if (index == -1) return "";
		return folder.substring(0, index);
	}


	private MatchCounter matchCounterFor(String folder) {
		return countersByFolder.get(folder, new Producer<MatchCounter>() { @Override public MatchCounter produce() throws RuntimeException {
			return new MatchCounter();
		}});
	}
}

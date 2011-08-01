package dfcsantos.tracks.tastematching.impl;

import java.util.Map;

public class Rating {

	private int endorsementCount;
	private int points;

	
	public Rating(Map<String, MatchCounter> countersByFolder, String parent) {
		for (String candidate : countersByFolder.keySet())
			if (contains(parent, candidate))
				accumulateRating(countersByFolder.get(candidate));
	}

	
	private boolean contains(String parent, String candidate) {
		if (candidate.equals(parent)) return true;
		if (parent.isEmpty()) return true; //Root folder.
		return candidate.startsWith(parent + "/");
	}

	
	private void accumulateRating(MatchCounter matchCounter) {
		if (matchCounter == null) return;
		endorsementCount += matchCounter.endorsementCount;
		points += matchCounter.points;
	}

	
	float result() {
		return points == 0
			? 0f
			: ((float)points) / endorsementCount;
	}

}

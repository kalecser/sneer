package dfcsantos.tracks.tastematching.impl;


class FolderMatchCounter {

	private int endorsementCount;
	private int matchCount;

	
	float matchRating() {
		return ((float) matchCount) / endorsementCount;
	}


	void countOpinion(Boolean opinion) {
		endorsementCount++;
		if (opinion == null) return;
		if (opinion)
			matchCount++;
		else
			matchCount--;
	}

}

package dfcsantos.tracks.tastematching.impl;

class FolderMatchCounter {

	private int _endorsementCount;
	private int _matchCount;

	void incrementEndorsementCount() {
		_endorsementCount++;
	}
	
	void incrementMatchCount() {
		_matchCount++;
	}

	float matchRating() {
		return _matchCount / _endorsementCount;
	}

}

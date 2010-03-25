package dfcsantos.tracks.tastematching.impl;

class FolderMatchCounter {

	private int _endorsementCount;
	private int _matchCount;

	int endorsementCount() {
		return _endorsementCount;
	}

	void incrementEndorsementCount() {
		_endorsementCount++;
	}
	
	int matchCount() {
		return _matchCount;
	}

	void incrementMatchCount() {
		_matchCount++;
	}

	float matchRating() {
		return _matchCount / _endorsementCount;
	}

}

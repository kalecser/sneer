package dfcsantos.tracks.tastematching.impl;


class MatchCounter {

	int endorsementCount;
	int points;

	
	void countOpinion(Boolean opinion) {
		endorsementCount++;
		if (opinion == null) return;
		if (opinion)
			points++;
		else
			points--;
	}

}

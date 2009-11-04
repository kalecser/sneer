package dfcsantos.tracks.tastematcher.impl;

import dfcsantos.tracks.assessment.TrackAssessment;
import dfcsantos.tracks.tastematcher.MusicalTasteMatcher;

class MusicalTasteMatcherImpl implements MusicalTasteMatcher {

	@Override
	public void consume(TrackAssessment assessment) {
		register(assessment);
	}

	private void register(TrackAssessment assesment) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(assesment.toString()); // Implement
	}

}

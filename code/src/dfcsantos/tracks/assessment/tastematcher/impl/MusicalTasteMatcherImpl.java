package dfcsantos.tracks.assessment.tastematcher.impl;

import dfcsantos.tracks.assessment.TrackAssessment;
import dfcsantos.tracks.assessment.tastematcher.MusicalTasteMatcher;

class MusicalTasteMatcherImpl implements MusicalTasteMatcher {

	@Override
	public void consume(TrackAssessment assessment) {
		register(assessment);
	}

	private void register(TrackAssessment assesment) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(assesment.toString()); // Implement
	}

}

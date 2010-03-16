package dfcsantos.tracks.assessment.assessor.impl;

import dfcsantos.tracks.Track;

class TrackRejection extends AbstractTrackAssessment {

	TrackRejection(Track assessed) {
		super(assessed);
	}

	@Override
	public int score() {
		return -1;
	}

}

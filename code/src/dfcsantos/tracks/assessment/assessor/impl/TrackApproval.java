package dfcsantos.tracks.assessment.assessor.impl;

import dfcsantos.tracks.Track;

class TrackApproval extends AbstractTrackAssessment {

	TrackApproval(Track assessed) {
		super(assessed);
	}

	@Override
	public int score() {
		return 1;
	}

}

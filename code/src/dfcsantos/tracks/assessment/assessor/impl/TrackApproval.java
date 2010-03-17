package dfcsantos.tracks.assessment.assessor.impl;

import dfcsantos.tracks.Track;

class TrackApproval extends AbstractTrackAssessment {

	TrackApproval(Track toBeAssessed) {
		super(toBeAssessed);
	}

	@Override
	public int score() {
		return 1;
	}

	@Override
	public String toString() {
		return super.toString() + " approved --> " + "(" + trackSourceName() + ", " + score() + ")";
	}

}

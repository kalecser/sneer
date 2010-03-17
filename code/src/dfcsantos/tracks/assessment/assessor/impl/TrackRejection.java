package dfcsantos.tracks.assessment.assessor.impl;

import dfcsantos.tracks.Track;

class TrackRejection extends AbstractTrackAssessment {

	TrackRejection(Track toBeAssessed) {
		super(toBeAssessed);
	}

	@Override
	public int score() {
		return -1;
	}

	@Override
	public String toString() {
		return super.toString() + " rejected --> " + "(" + trackSourceName() + ", " + score() + ")";
	}

}

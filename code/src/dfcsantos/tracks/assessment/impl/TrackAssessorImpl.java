package dfcsantos.tracks.assessment.impl;

import dfcsantos.tracks.Track;
import dfcsantos.tracks.assessment.TrackAssessment;
import dfcsantos.tracks.assessment.TrackAssessor;

public class TrackAssessorImpl implements TrackAssessor {

	@Override
	public TrackAssessment approve(Track track) {
		return new TrackApproval(track);
	}

	@Override
	public TrackAssessment reject(Track track) {
		return new TrackRejection(track);
	}

}

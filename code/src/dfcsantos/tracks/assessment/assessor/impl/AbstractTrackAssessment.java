package dfcsantos.tracks.assessment.assessor.impl;

import dfcsantos.tracks.Track;
import dfcsantos.tracks.assessment.TrackAssessment;

abstract class AbstractTrackAssessment implements TrackAssessment {

	Track _assessed;

	AbstractTrackAssessment(Track assessed) {
		_assessed = assessed;
	}

}

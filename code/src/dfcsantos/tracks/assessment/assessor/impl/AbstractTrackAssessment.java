package dfcsantos.tracks.assessment.assessor.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.io.log.Logger;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.assessment.TrackAssessment;

abstract class AbstractTrackAssessment implements TrackAssessment {

	Track _assessed;

	AbstractTrackAssessment(Track assessed) {
		_assessed = assessed;
		my(Logger.class).log(this.toString());
	}

	@Override
	public Track track() {
		return _assessed;
	}

}

package dfcsantos.tracks.assessment.assessor.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.assessment.TrackAssessment;
import dfcsantos.tracks.assessment.assessor.TrackAssessor;

class TrackAssessorImpl implements TrackAssessor {

	private final Register<TrackAssessment> _lastAssessment = my(Signals.class).newRegister(null); 

	@Override
	synchronized
	public TrackAssessment approve(Track track) {
		TrackAssessment assessment = new TrackApproval(track);
		_lastAssessment.setter().consume(assessment);
		return assessment;
	}

	@Override
	synchronized
	public TrackAssessment reject(Track track) {
		TrackAssessment assessment = new TrackRejection(track);
		_lastAssessment.setter().consume(assessment);
		return assessment;
	}

	@Override
	public Signal<TrackAssessment> lastAssessment() {
		return _lastAssessment.output();
	}

}

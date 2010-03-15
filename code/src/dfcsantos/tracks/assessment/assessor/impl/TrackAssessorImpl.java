package dfcsantos.tracks.assessment.assessor.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.assessment.TrackAssessment;
import dfcsantos.tracks.assessment.assessor.TrackAssessor;

class TrackAssessorImpl implements TrackAssessor {

	private ListRegister<TrackAssessment> _assessments = my(CollectionSignals.class).newListRegister();

	@Override
	public TrackAssessment approve(Track track) {
		TrackAssessment assessment = new TrackApproval(track);
		_assessments.add(assessment);
		return assessment;
	}

	@Override
	public TrackAssessment reject(Track track) {
		TrackAssessment assessment = new TrackRejection(track);
		_assessments.add(assessment);
		return assessment;
	}

	@Override
	public ListSignal<TrackAssessment> assessements() {
		return _assessments.output();
	}

}

package dfcsantos.tracks.assessment.tastematcher.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.assessment.TrackAssessment;
import dfcsantos.tracks.assessment.assessor.TrackAssessor;
import dfcsantos.tracks.assessment.tastematcher.MusicalTasteMatcher;

class MusicalTasteMatcherImpl implements MusicalTasteMatcher {

	@SuppressWarnings("unused") private final WeakContract _toAvoidGC;

	{
		_toAvoidGC = my(TrackAssessor.class).assessements().addReceiver(new Consumer<CollectionChange<TrackAssessment>>() { @Override public void consume(CollectionChange<TrackAssessment> changes) {
			for (TrackAssessment assessment : changes.elementsAdded()) {
				register(assessment);
			}
		}});
	}

	@Override
	public void consume(TrackAssessment assessment) {
		register(assessment);
	}

	private void register(TrackAssessment assesment) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(assesment.toString()); // Implement
	}

}

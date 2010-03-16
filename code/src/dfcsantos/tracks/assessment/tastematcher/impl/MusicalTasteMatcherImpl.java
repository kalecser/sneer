package dfcsantos.tracks.assessment.tastematcher.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReferences;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.assessment.TrackAssessment;
import dfcsantos.tracks.assessment.assessor.TrackAssessor;
import dfcsantos.tracks.assessment.tastematcher.MusicalTasteMatcher;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

class MusicalTasteMatcherImpl implements MusicalTasteMatcher {

	private final Map<Contact, Integer> _scoreByContact = new ConcurrentHashMap<Contact, Integer>();

	private final ImmutableReference<Signal<Boolean>> _onOffSwitch = my(ImmutableReferences.class).newInstance();

	private WeakContract _assessmentsConsumerContract;

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;

	@Override
	public void setOnOffSwitch(Signal<Boolean> onOffSwitch) {
		_onOffSwitch.set(onOffSwitch);

		_toAvoidGC = onOffSwitch.addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean isOn) {
			react(isOn);
		}});
	}

	private void react(boolean isOn) {
		if(isOn)
			startListeningToAssessments();
		else
			stopListeningToAssessments();
	}

	private void startListeningToAssessments() {
		_toAvoidGC = my(TrackAssessor.class).lastAssessment().addReceiver(new Consumer<TrackAssessment>() { @Override public void consume(TrackAssessment assessment) {
			register(assessment);
		}});
	}

	private void stopListeningToAssessments() {
		if (_assessmentsConsumerContract == null) return;
		_assessmentsConsumerContract.dispose();
		_assessmentsConsumerContract = null;
	}

	private void register(TrackAssessment assessment) {
		if (assessment == null) return;

		Contact peer = assessedPeer(assessment);
		if (peer == null) {
			my(Logger.class).log("Unable to assess track received from an unidentified peer: ", assessment.track());
			return;
		}
		_scoreByContact.put(peer, assessment.score());
	}

	private Contact assessedPeer(TrackAssessment assessment) {
		File tracksFolder = assessment.track().file().getParentFile();
		if (sharedTracksFolder().equals(tracksFolder)) return null;
		return my(Contacts.class).contactGiven(tracksFolder.getName());
	}

	private File sharedTracksFolder() {
		return my(TracksFolderKeeper.class).sharedTracksFolder().currentValue();
	}

}

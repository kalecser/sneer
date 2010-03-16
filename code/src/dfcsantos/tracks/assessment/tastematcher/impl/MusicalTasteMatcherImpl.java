package dfcsantos.tracks.assessment.tastematcher.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReferences;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.assessment.TrackAssessment;
import dfcsantos.tracks.assessment.assessor.TrackAssessor;
import dfcsantos.tracks.assessment.tastematcher.MusicalTasteMatcher;
import dfcsantos.tracks.storage.folder.TracksFolderKeeper;

class MusicalTasteMatcherImpl implements MusicalTasteMatcher {

	private final Map<Contact, Integer> _scoreByContact = new ConcurrentHashMap<Contact, Integer>();
	private final Register<Contact> _topScorer = my(Signals.class).newRegister(randomContact());

	private final ImmutableReference<Signal<Boolean>> _onOffSwitch = my(ImmutableReferences.class).newInstance();

	private WeakContract _assessmentsConsumerContract;

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;

	private static Contact randomContact() {
		// Fix: think of the best way to initialize _topScorer
		return null;
//		return my(Contacts.class).contacts().currentElements().iterator().next();
	}

	@Override
	public void setOnOffSwitch(Signal<Boolean> onOffSwitch) {
		_onOffSwitch.set(onOffSwitch);

		_toAvoidGC = onOffSwitch.addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean isOn) {
			react(isOn);
		}});
	}

	@Override
	public Signal<Contact> bestMatch() {
		return _topScorer.output();
	}

	private void react(boolean isOn) {
		if(isOn)
			startListeningToAssessments();
		else
			stopListeningToAssessments();
	}

	private void startListeningToAssessments() {
		my(Logger.class).log("Activating MusicalTasteMatcher");
		_toAvoidGC = my(TrackAssessor.class).lastAssessment().addReceiver(new Consumer<TrackAssessment>() { @Override public void consume(TrackAssessment assessment) {
			register(assessment);
		}});
	}

	private void stopListeningToAssessments() {
		if (_assessmentsConsumerContract == null) return;
		my(Logger.class).log("Deactivating MusicalTasteMatcher");
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
		my(Logger.class).log("Registering assessment for {}: ({}, {})", assessment.track().name(), peer, assessment.score());
		_scoreByContact.put(peer, assessment.score());
		updateTopScorerIfNecessary();
	}

	private Contact assessedPeer(TrackAssessment assessment) {
		File tracksFolder = assessment.track().file().getParentFile();
		if (sharedTracksFolder().equals(tracksFolder)) return null;
		return my(Contacts.class).contactGiven(tracksFolder.getName());
	}

	private File sharedTracksFolder() {
		return my(TracksFolderKeeper.class).sharedTracksFolder().currentValue();
	}

	private void updateTopScorerIfNecessary() {
		Contact topScorer = topScorer();
		if (topScorer == null) return;
		_topScorer.setter().consume(topScorer);
	}

	private Contact topScorer() {
		Contact topScorer = null;
		Integer topScore = topScore();
		if (topScore == null) topScore = Integer.MIN_VALUE;
		for(Entry<Contact, Integer> scoreByContact : _scoreByContact.entrySet()) {
			int score = scoreByContact.getValue();
			if (score > topScore)
				topScorer = scoreByContact.getKey();
		}
		return topScorer;
	}

	private Integer topScore() {
		Contact topScorer = _topScorer.output().currentValue();
		if (topScorer == null) return null;
		return _scoreByContact.get(topScorer);
	}

}

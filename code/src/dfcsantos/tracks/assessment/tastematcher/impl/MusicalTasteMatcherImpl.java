package dfcsantos.tracks.assessment.tastematcher.impl;

import static sneer.foundation.environments.Environments.my;

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

class MusicalTasteMatcherImpl implements MusicalTasteMatcher {

	private final ImmutableReference<Signal<Boolean>> _onOffSwitch = my(ImmutableReferences.class).newInstance();

	private final Map<Contact, Integer> _scoresByContact = new ConcurrentHashMap<Contact, Integer>();
	private final Register<Contact> _topScorer = my(Signals.class).newRegister(randomContact());

	private WeakContract _assessmentsListenerCtr;

	@SuppressWarnings("unused") private WeakContract _onOffSwitchListenerCtr;

	MusicalTasteMatcherImpl() {
		restore();
	}

	@Override
	public void setOnOffSwitch(Signal<Boolean> onOffSwitch) {
		_onOffSwitch.set(onOffSwitch);

		_onOffSwitchListenerCtr = onOffSwitch.addReceiver(new Consumer<Boolean>() { @Override public void consume(Boolean isOn) {
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
		_onOffSwitchListenerCtr = my(TrackAssessor.class).lastAssessment().addReceiver(new Consumer<TrackAssessment>() { @Override public void consume(TrackAssessment assessment) {
			register(assessment);
		}});
	}

	private void stopListeningToAssessments() {
		if (_assessmentsListenerCtr == null) return;
		my(Logger.class).log("Deactivating MusicalTasteMatcher");
		_assessmentsListenerCtr.dispose();
		_assessmentsListenerCtr = null;
	}

	private void register(TrackAssessment assessment) {
		if (assessment == null) return;

		Contact peer = assessment.trackSource();
		if (peer == null) {
			my(Logger.class).log("Unable to assess track received from an unidentified peer: ", assessment.track());
			return;
		}
		my(Logger.class).log("Registering assessment for {}: ({}, {})", assessment.track().name(), peer, assessment.score());
		add(peer, assessment.score());
		updateTopScorerIfNecessary();
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
		for(Entry<Contact, Integer> scoreByContact : _scoresByContact.entrySet()) {
			int score = scoreByContact.getValue();
			if (score > topScore)
				topScorer = scoreByContact.getKey();
		}
		return topScorer;
	}

	private Integer topScore() {
		Contact topScorer = _topScorer.output().currentValue();
		if (topScorer == null) return null;
		return _scoresByContact.get(topScorer);
	}

	private static Contact randomContact() {
		// Fix: think of the best way to initialize _topScorer
		// return my(Contacts.class).contacts().currentElements().iterator().next();
		return null;
	}

	private void restore() {
		for (Object[] scoreByContact : Store.restore()) {
			add(
				my(Contacts.class).contactGiven((String) scoreByContact[0]),
				(Integer) scoreByContact[1]
			);
		}
	}

	private void add(Contact peer, Integer score) {
		_scoresByContact.put(peer, score);
		save();
	}

	private void save() {
		Store.save(_scoresByContact.entrySet());
	}

}

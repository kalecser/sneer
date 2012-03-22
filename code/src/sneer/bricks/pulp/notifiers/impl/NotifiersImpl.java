package sneer.bricks.pulp.notifiers.impl;

import basis.lang.Producer;
import sneer.bricks.pulp.notifiers.Notifier;
import sneer.bricks.pulp.notifiers.Notifiers;

class NotifiersImpl implements Notifiers {

	@Override
	public <T> Notifier<T> newInstance() {
		return newInstance(null);
	}

	@Override
	public <T> Notifier<T> newInstance(Producer<? extends T> welcomeEventProducer) {
		return new NotifierImpl<T>(welcomeEventProducer);
	}

}
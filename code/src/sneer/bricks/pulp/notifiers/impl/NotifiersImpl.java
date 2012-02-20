package sneer.bricks.pulp.notifiers.impl;

import sneer.bricks.pulp.notifiers.Notifier;
import sneer.bricks.pulp.notifiers.Notifiers;
import sneer.foundation.lang.Producer;

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
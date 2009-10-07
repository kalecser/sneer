package sneer.bricks.pulp.events.impl;

import sneer.bricks.pulp.events.EventNotifier;
import sneer.bricks.pulp.events.EventNotifiers;
import sneer.foundation.lang.Producer;

class EventNotifiersImpl implements EventNotifiers {

	@Override
	public <T> EventNotifier<T> newInstance() {
		return newInstance(null);
	}

	@Override
	public <T> EventNotifier<T> newInstance(Producer<? extends T> welcomeEventProducer) {
		return new EventNotifierImpl<T>(welcomeEventProducer);
	}

}
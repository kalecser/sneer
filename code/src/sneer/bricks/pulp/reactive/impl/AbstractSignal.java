//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Fabio Roger Manera.

package sneer.bricks.pulp.reactive.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.notifiers.Notifier;
import sneer.bricks.pulp.notifiers.Notifiers;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Producer;

abstract class AbstractSignal<T> implements Signal<T> {

	Notifier<T> _notifier = my(Notifiers.class).newInstance(new Producer<T>(){@Override public T produce() {
		return currentValue();
	}});

	@Override
	public String toString() {
		T currentValue = currentValue();
		if (currentValue == null) return "null";
		return currentValue.toString();
	}

	protected void notifyReceivers(T value) {
		_notifier.notifyReceivers(value);
	}

	@Override
	public WeakContract addReceiver(Consumer<? super T> eventReceiver) {
		return _notifier.output().addReceiver(eventReceiver);
	}

	@Override
	public WeakContract addPulseReceiver(Runnable pulseReceiver) {
		return _notifier.output().addPulseReceiver(pulseReceiver);
	}
	
}

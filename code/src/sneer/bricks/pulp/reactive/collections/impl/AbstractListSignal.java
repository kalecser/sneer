package sneer.bricks.pulp.reactive.collections.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.notifiers.Notifier;
import sneer.bricks.pulp.notifiers.Notifiers;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.pulp.reactive.collections.ListChange;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.pulp.reactive.collections.ListChange.Visitor;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Producer;

abstract class AbstractListSignal<T> implements ListSignal<T> {

	
	Notifier<ListChange<T>> _notifierAsList = my(Notifiers.class).newInstance(new Producer<ListChange<T>>(){@Override public ListChange<T> produce() {
		return currentElementsAsListChange();
	}});
	
	Notifier<CollectionChange<T>> _notifierAsCollection = my(Notifiers.class).newInstance(new Producer<CollectionChange<T>>(){@Override public CollectionChange<T> produce() {
		return currentElementsAsCollectionChange();
	}});

	
	@Override
	public WeakContract addPulseReceiver(Runnable pulseReceiver) {
		return _notifierAsCollection.output().addPulseReceiver(pulseReceiver);
	}

	
	@Override
	public WeakContract addListReceiver(Consumer<? super ListChange<T>> receiver) {
		return _notifierAsList.output().addReceiver(receiver);
	}

	
	@Override
	public WeakContract addListReceiverAsVisitor(Visitor<T> visitor) {
		return addListReceiver(new VisitingListReceiver<T>(visitor));
	}
	
	
	@Override
	public WeakContract addReceiver(Consumer<? super CollectionChange<T>> receiver) {
		return _notifierAsCollection.output().addReceiver(receiver);
	}
	
	
	protected abstract CollectionChange<T> currentElementsAsCollectionChange();
	protected abstract ListChange<T> currentElementsAsListChange();
	
	
	void notifyReceivers(final AbstractListValueChange<T> valueChange) {
		_notifierAsList.notifyReceivers(valueChange);
		_notifierAsCollection.notifyReceivers(valueChange);
	}
}

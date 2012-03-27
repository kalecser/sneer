//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Kalecser Kurtz, Fabio Roger Manera.

package sneer.bricks.pulp.reactive.collections.impl;

import static basis.environments.Environments.my;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.notifiers.Notifier;
import sneer.bricks.pulp.notifiers.Notifiers;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import basis.lang.Consumer;
import basis.lang.Producer;



class SetRegisterImpl<T> implements SetRegister<T> {


	private class MyOutput implements SetSignal<T> {

		private final Notifier<CollectionChange<T>> _notifier = my(Notifiers.class).newInstance(new Producer<CollectionChange<T>>(){@Override public CollectionChange<T> produce() {
			return new CollectionChangeImpl<T>(contentsCopy(), null);
		}});

		@Override
		public WeakContract addPulseReceiver(Runnable pulseReceiver) {
			throw new basis.lang.exceptions.NotImplementedYet(); // Implement
		}

		@Override
		public WeakContract addReceiver(Consumer<? super CollectionChange<T>> eventReceiver) {
			return _notifier.output().addReceiver(eventReceiver);
		}
		
		@Override
		public Collection<T> currentElements() {
			return contentsCopy();
		}

		@Override
		public Iterator<T> iterator() {
			return contentsCopy().iterator();
		}

		private Set<T> contentsCopy() {
			synchronized (_contents) {
				return new HashSet<T>(_contents);
			}
		}

		@Override
		public Signal<Integer> size() {
			return _size.output();
		}

		@Override
		public boolean currentContains(T element) {
			return _contents.contains(element);
		}

	}

	private final Set<T> _contents = new HashSet<T>();
	private final Register<Integer> _size = my(Signals.class).newRegister(0);

	private final MyOutput _output = new MyOutput();

	
	@Override
	public SetSignal<T> output() {
		return _output;
	}

	@Override
	public void add(T elementAdded) {
		change(new CollectionChangeImpl<T>(elementAdded, null));
	}

	@Override
	public void remove(T elementRemoved) {
		change(new CollectionChangeImpl<T>(null, elementRemoved));
	}

	@Override
	public void change(CollectionChange<T> change) {
		synchronized (_contents) {
			preserveOnlyActualChanges(change);
			if (change.elementsAdded().isEmpty() && change.elementsRemoved().isEmpty())
				return;
			
			_contents.addAll(change.elementsAdded());
			_contents.removeAll(change.elementsRemoved());
			_output._notifier.notifyReceivers(change);
			
			updateSize();
		}
	}

	private void updateSize() {
		Integer size = _contents.size();
		if (size != _size.output().currentValue())
			_size.setter().consume(size);
	}

	private void preserveOnlyActualChanges(CollectionChange<T> change) {
		change.elementsAdded().removeAll(_contents);
		change.elementsRemoved().retainAll(_contents);
		change.elementsAdded().removeAll(change.elementsRemoved());
	}

	@Override
	public void addAll(Collection<T> elements) {
		change(new CollectionChangeImpl<T>(elements, null));
	}

}

package sneer.bricks.pulp.reactive.signalchooser.impl;

import static basis.environments.Environments.my;

import java.util.HashMap;
import java.util.Map;

import basis.lang.Closure;
import basis.lang.Consumer;
import basis.lang.exceptions.NotImplementedYet;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.notifiers.pulsers.PulseSenders;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.pulp.reactive.collections.CollectionSignal;
import sneer.bricks.pulp.reactive.signalchooser.ListOfSignalsReceiver;
import sneer.bricks.pulp.reactive.signalchooser.SignalChooser;

class SignalChooserReceiver<T> {
	
	@SuppressWarnings("unused")	private final Object _refToAvoidGc;

	private final Map<T, ElementReceiver> _receiversByElement = new HashMap<T, ElementReceiver>();
	private final ListOfSignalsReceiver<T> _listOfSignalsReceiver;
	
	public SignalChooserReceiver(CollectionSignal<T> input, ListOfSignalsReceiver<T> listOfSignalsReceiver) {
		_listOfSignalsReceiver = listOfSignalsReceiver;
		_refToAvoidGc = input.addReceiver ( new Consumer<CollectionChange<T>>() { @Override public void consume(CollectionChange<T> change) {
			receiveChange(change);
		}});
	}

	synchronized
	private void receiveChange(CollectionChange<T> change) {
		for(T element: change.elementsRemoved())
			SignalChooserReceiver.this.elementRemoved(element);
			
		for(T element: change.elementsAdded())
			SignalChooserReceiver.this.elementAdded(element);
	}


	private void elementRemoved(T element) {
		if (signalChooser() == null) return;
		ElementReceiver receiver = _receiversByElement.remove(element);
		if (receiver == null) return;
		receiver._isActive = false; //Refactor: Dispose the reception (if supported) instead of setting false.
	}

	private void elementAdded(T element) {
		if (signalChooser() == null) return;
			
		ElementReceiver receiver = new ElementReceiver(element);
		if (_receiversByElement.put(element, receiver) != null)
			throw new NotImplementedYet("Duplicated elements are not supported since there is no tracking yet of the number of occurences. Duplicate element: " + element);
	}
	
	private SignalChooser<T> signalChooser() {
		return _listOfSignalsReceiver.signalChooser();
	}
	
	private class ElementReceiver {
		private boolean _isActive = false;
		@SuppressWarnings("unused") private final WeakContract _referenceToAvoidGc;

		ElementReceiver(final T element) {
			_referenceToAvoidGc = my(PulseSenders.class).receive(new Closure(){ @Override public void run() {
				if (!_isActive) return;
				_listOfSignalsReceiver.elementSignalChanged(element);
			}}, signalChooser().signalsToReceiveFrom(element));
			
			startNotifyingReceiver();
		}

		private void startNotifyingReceiver() {
			_isActive = true;
		}
	}
	
}
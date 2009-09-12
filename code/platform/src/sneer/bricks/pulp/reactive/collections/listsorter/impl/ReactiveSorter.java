package sneer.bricks.pulp.reactive.collections.listsorter.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.pulp.reactive.collections.CollectionSignal;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.pulp.reactive.signalchooser.ListOfSignalsReceiver;
import sneer.bricks.pulp.reactive.signalchooser.SignalChooser;
import sneer.bricks.pulp.reactive.signalchooser.SignalChoosers;
import sneer.foundation.lang.Consumer;

final class ReactiveSorter<T> implements ListOfSignalsReceiver<T>{

	private static final SignalChoosers SignalChoosers = my(SignalChoosers.class);

	
	private final CollectionSignal<T> _input;
	private final SignalChooser<T> _chooser;	
	private final Comparator<T> _comparator;
	
	private final ListRegister<T> _output;
	
	@SuppressWarnings("unused") private final Object _refToAvoidGc;
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc2;
	
	
	ReactiveSorter(CollectionSignal<T> input, Comparator<T> comparator, SignalChooser<T> chooser) {
		_input = input;
		_comparator = comparator;
		_chooser = chooser;
		_output = my(CollectionSignals.class).newListRegister();
		
		_refToAvoidGc2 = _input.addReceiver(new Consumer<CollectionChange<T>>(){ @Override public void consume(CollectionChange<T> change) {
			inputChanged(change);
		}});
		
		_refToAvoidGc = SignalChoosers.receive(input, this);
	}

	
	@Override public void elementSignalChanged(T element) { move(element); }
	@Override public SignalChooser<T> signalChooser() { return _chooser; }
	
	
	ListSignal<T> output() {
		return my(WeakReferenceKeeper.class).keep(_output.output(), this);
	}
	
	
	synchronized
	private void move(T element) {
		_output.move(
			_output.output().currentIndexOf(element),
			findPositionToMove(element)
		);
	}
	
	
	synchronized
	private void inputChanged(CollectionChange<T> change) {
		for (T element : change.elementsAdded()  )
			_output.addAt(findPositionToInsert(element), element);
		
		for (T element : change.elementsRemoved())
			_output.remove(element);
	}


	private int findPositionToMove(T element) {
		List<T> copy = _output.output().currentElements(); //Optimize
		Collections.sort(copy, _comparator);
		
		return copy.indexOf(element);
	}
	
	
	private int findPositionToInsert(T element) {
		List<T> copy = _output.output().currentElements(); //Optimize
		copy.add(element);
		Collections.sort(copy, _comparator);
		
		return copy.indexOf(element);
	}

	
}
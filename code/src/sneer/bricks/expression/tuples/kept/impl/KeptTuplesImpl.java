package sneer.bricks.expression.tuples.kept.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.kept.KeptTuples;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.foundation.lang.Consumer;

public class KeptTuplesImpl implements KeptTuples {

	private ListRegister<Tuple> _delegate = my(CollectionSignals.class).newListRegister();

	
	@Override
	public void add(Tuple element) {
		_delegate.add(element);
	}

	@Override
	public void addAt(int index, Tuple element) {
		_delegate.addAt(index, element);
	}

	@Override
	public Consumer<Tuple> adder() {
		return _delegate.adder();
	}

	@Override
	public void move(int oldIndex, int newIndex) {
		_delegate.move(oldIndex, newIndex);
	}

	@Override
	public 	ListSignal<Tuple> output() {
		return _delegate.output();
	}

	@Override
	public void remove(Tuple element) {
		_delegate.remove(element);
	}

	@Override
	public void removeAt(int index) {
		_delegate.removeAt(index);
	}

	@Override
	public Consumer<Tuple> remover() {
		return _delegate.remover();
	}

	@Override
	public void replace(int index, Tuple newElement) {
		_delegate.replace(index, newElement);
	}
	
}

package sneer.bricks.expression.tuples.kept.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.kept.KeptTuples;
import sneer.bricks.hardware.io.prevalence.map.ExportMap;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.foundation.lang.Consumer;

public class KeptTuplesImpl implements KeptTuples {

	private ListRegister<Tuple> _delegate = my(CollectionSignals.class).newListRegister();
	private Consumer<Tuple> _adder = register(_delegate.adder());
	private Consumer<Tuple> _remover = register(_delegate.remover());

	public void add(Tuple element) {
		_delegate.add(element);
	}

	public void addAt(int index, Tuple element) {
		_delegate.addAt(index, element);
	}

	public Consumer<Tuple> adder() {
		return _adder;
	}

	public void move(int oldIndex, int newIndex) {
		_delegate.move(oldIndex, newIndex);
	}

	public 	ListSignal<Tuple> output() {
		return _delegate.output();
	}

	public void remove(Tuple element) {
		_delegate.remove(element);
	}

	public void removeAt(int index) {
		_delegate.removeAt(index);
	}

	public Consumer<Tuple> remover() {
		return _remover;
	}

	public void replace(int index, Tuple newElement) {
		_delegate.replace(index, newElement);
	}
	
	private <T> T register(T object) {
		return my(ExportMap.class).register(object);
	}

}

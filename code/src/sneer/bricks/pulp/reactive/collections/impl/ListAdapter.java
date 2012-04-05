package sneer.bricks.pulp.reactive.collections.impl;

import static basis.environments.Environments.my;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListChange.Visitor;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import basis.lang.Functor;

class ListAdapter<IN, OUT> {

	private ListRegister<OUT> _register = my(CollectionSignals.class).newListRegister();

	@SuppressWarnings("unused") private final Object _referenceToAvoidGc;

	ListAdapter(ListSignal<IN> input, final Functor<IN, OUT> functor) {
		_referenceToAvoidGc = input.addListReceiverAsVisitor(new Visitor<IN>() {

			@Override
			public void elementAdded(int index, IN element) {
				_register.addAt(index, functor.evaluate(element));
			}

			@Override
			public void elementRemoved(int index, IN element) {
				_register.removeAt(index);
			}

			@Override
			public void elementReplaced(int index, IN oldElement, IN newElement) {
				_register.replace(index, functor.evaluate(newElement));
			}

			@Override
			public void elementMoved(int index, int newIndex, IN newElement) {
				_register.move(index, newIndex);
			}
		});
	}

	ListSignal<OUT> output() {
		return my(WeakReferenceKeeper.class).keep(_register.output(), this);
	}

}

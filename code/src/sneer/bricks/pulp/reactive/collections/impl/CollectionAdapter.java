package sneer.bricks.pulp.reactive.collections.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.pulp.reactive.collections.CollectionSignal;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;

class CollectionAdapter<IN, OUT> {

	private ListRegister<OUT> _register = my(CollectionSignals.class).newListRegister();

	@SuppressWarnings("unused") private final Object _referenceToAvoidGc;

	CollectionAdapter(CollectionSignal<IN> input, final Functor<IN, OUT> functor) {
		_referenceToAvoidGc = input.addReceiver(new Consumer<CollectionChange<IN>>() { @Override public void consume(CollectionChange<IN> inputValues) {
			for(IN elementAdded : inputValues.elementsAdded())
				_register.add(functor.evaluate(elementAdded));

			for(IN elementRemoved : inputValues.elementsRemoved())
				_register.remove(functor.evaluate(elementRemoved));
		}});
	}

	CollectionSignal<OUT> output() {
		return my(WeakReferenceKeeper.class).keep(_register.output(), this);
	}

}

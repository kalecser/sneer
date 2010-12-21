package sneer.bricks.pulp.reactive.collections.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.ram.collections.CollectionUtils;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.pulp.reactive.collections.CollectionSignal;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;

class CollectionAdapter<IN, OUT> {

	private SetRegister<OUT> _register = my(CollectionSignals.class).newSetRegister();

	@SuppressWarnings("unused") private final Object _referenceToAvoidGc;

	CollectionAdapter(CollectionSignal<IN> input, final Functor<IN, OUT> functor) {
		_referenceToAvoidGc = input.addReceiver(new Consumer<CollectionChange<IN>>() { @Override public void consume(final CollectionChange<IN> inputValues) {
			_register.change(
				new CollectionChangeImpl<OUT>(
					my(CollectionUtils.class).map(inputValues.elementsAdded(), functor),
					my(CollectionUtils.class).map(inputValues.elementsRemoved(), functor)
				)
			);
		}});
	}

	CollectionSignal<OUT> output() {
		return my(WeakReferenceKeeper.class).keep(_register.output(), this);
	}

}

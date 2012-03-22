package sneer.bricks.pulp.reactive.collections.impl;

import basis.lang.Functor;
import sneer.bricks.pulp.reactive.collections.CollectionSignal;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.pulp.reactive.collections.MapRegister;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.pulp.reactive.collections.SetSignal;

class CollectionSignalsImpl implements CollectionSignals {

	@Override
	public <T> ListRegister<T> newListRegister() {
		return new ListRegisterImpl<T>();
	}
	
	@Override
	public <T> SetRegister<T> newSetRegister() {
		return new SetRegisterImpl<T>();
	}
	
	@Override
	public <K, V> MapRegister<K, V> newMapRegister() {
		return new MapRegisterImpl<K, V>();
	}

	@Override
	public <A, B> ListSignal<B> adapt(ListSignal<A> input, Functor<A, B> functor) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public <A, B> SetSignal<B> adapt(CollectionSignal<A> input, Functor<A, B> functor) {
		return new CollectionAdapter<A, B>(input, functor).output();
	}

}

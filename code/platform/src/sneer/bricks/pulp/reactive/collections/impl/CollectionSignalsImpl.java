package sneer.bricks.pulp.reactive.collections.impl;

import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.MapRegister;
import sneer.bricks.pulp.reactive.collections.SetRegister;

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

}

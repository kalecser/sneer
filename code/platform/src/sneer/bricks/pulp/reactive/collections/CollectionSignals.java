package sneer.bricks.pulp.reactive.collections;

import sneer.foundation.brickness.Brick;

@Brick
public interface CollectionSignals {

	<T> ListRegister<T> newListRegister();
	<T> SetRegister<T> newSetRegister();
	<K, V> MapRegister<K, V> newMapRegister();
	
}

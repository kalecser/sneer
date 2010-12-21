package sneer.bricks.pulp.reactive.collections;

import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Functor;

@Brick
public interface CollectionSignals {

	<T> ListRegister<T> newListRegister();

	<T> SetRegister<T> newSetRegister();

	<K, V> MapRegister<K, V> newMapRegister();

	<A, B> CollectionSignal<B> adapt(CollectionSignal<A> input, Functor<A, B> functor);

}

package sneer.bricks.pulp.reactive.collections;

import basis.brickness.Brick;
import basis.lang.Functor;

@Brick
public interface CollectionSignals {

	<T> ListRegister<T> newListRegister();

	<T> SetRegister<T> newSetRegister();

	<K, V> MapRegister<K, V> newMapRegister();

	<A, B> ListSignal<B> adapt(ListSignal<A> input, Functor<A, B> functor);
	<A, B> SetSignal<B> adapt(CollectionSignal<A> input, Functor<A, B> functor);

}

package sneer.bricks.pulp.reactive;

import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;
import sneer.foundation.lang.FunctorX;
import sneer.foundation.lang.PickyConsumer;
import sneer.foundation.lang.exceptions.Refusal;

@Brick
public interface Signals {  

	<T> Register<T> newRegister(T initialValue);
	<T> Signal<T> constant(T value);

	Consumer<Object> sink();

	<A, B> Signal<B> adapt(Signal<A> input, Functor<A, B> functor);
	<A, B> Signal<B> adaptSignal(Signal<A> input, Functor<A, Signal<B>> functor);

	<A, B> Consumer<A> adapt(Consumer<B> delegate, Functor<A, B> functor);
	<A, B> PickyConsumer<A> adapt(PickyConsumer<B> delegate, FunctorX<A, B, Refusal> functor);

}

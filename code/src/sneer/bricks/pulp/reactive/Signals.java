package sneer.bricks.pulp.reactive;

import basis.brickness.Brick;
import basis.lang.Consumer;
import basis.lang.Functor;
import basis.lang.FunctorX;
import basis.lang.PickyConsumer;
import basis.lang.exceptions.Refusal;

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

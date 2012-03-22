package sneer.bricks.pulp.reactive.impl;

import basis.lang.Consumer;
import basis.lang.Functor;
import basis.lang.FunctorX;
import basis.lang.PickyConsumer;
import basis.lang.exceptions.Refusal;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;

class SignalsImpl implements Signals {

	
	private static final Consumer<Object> SINK = new Consumer<Object>() { @Override public void consume(Object ignored){} };

	
	@Override
	public <T> Signal<T> constant(T value) {
		return new ConstantImpl<T>(value);
	}

	
	@Override
	public Consumer<Object> sink() {
		return SINK;
	}

	
	@Override
	public <A, B> Signal<B> adapt(Signal<A> input, Functor<A, B> functor) {
		return new Adapter<A, B>(input, functor).output();
	}

	
	@Override
	public <A, B> Signal<B> adaptSignal(Signal<A> input, Functor<A, Signal<B>> functor) {
		return new SignalAdapter<A, B>(input, functor).output();
	}

	
	@Override
	public <T> Register<T> newRegister(T initialValue) {
		return new RegisterImpl<T>(initialValue);
	}

	
	@Override
	public <A, B> Consumer<A> adapt(final Consumer<B> delegate, final Functor<A, B> functor) {
		return new Consumer<A>() { @Override public void consume(A value) {
			delegate.consume(functor.evaluate(value));
		}};
	}

	
	@Override
	public <A, B> PickyConsumer<A> adapt(final PickyConsumer<B> delegate, final FunctorX<A, B, Refusal> functor) {
		return new PickyConsumer<A>() { @Override public void consume(A value) throws Refusal {
			delegate.consume(functor.evaluate(value));
		}};
	}

}

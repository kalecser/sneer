package sneer.bricks.pulp.notifiers.receivers.impl;

import basis.lang.Consumer;
import sneer.bricks.pulp.notifiers.Source;

public class Solder<T> {

	private final Consumer<? super T> _delegate;

	@SuppressWarnings("unused") private final Object _referenceToAvoidGc;

	public Solder(Source<? extends T> eventSource, Consumer<? super T> receiver) {
		_delegate = receiver;

		_referenceToAvoidGc = eventSource.addReceiver(new Consumer<T>() { @Override public void consume(T event) {
			_delegate.consume(event);
		}});
	}
}

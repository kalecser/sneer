package sneer.bricks.pulp.reactive.impl;

import java.lang.ref.WeakReference;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.lang.Consumer;

class RegisterImpl<T> implements Register<T> {

	class MyOutput extends AbstractSignal<T> {

		@Override
		synchronized
		public T currentValue() {
			return _currentValue;
		}
	}

	class MySetter implements Consumer<T> {

		@Override
		synchronized
		public void consume(T newValue) {
			if (isSameValue(newValue)) return;

			_currentValue = newValue;

			AbstractSignal<T> rawOutput = rawOutput();
			if (rawOutput == null) return;
			rawOutput.notifyReceivers(newValue);
		}
	}

	private T _currentValue;
	private WeakReference<AbstractSignal<T>> _output;

	RegisterImpl(T initialValue) {
		setter().consume(initialValue);
	}

	private boolean isSameValue(T value) {
		if (value == _currentValue) return true; 
		if (value != null && value.equals(_currentValue)) return true;

		return false;
	}

	@Override
	public synchronized Signal<T> output() {
		AbstractSignal<T> rawOutput = rawOutput();
		if (rawOutput != null) return rawOutput;

		MyOutput newOutput = new MyOutput();
		_output = new WeakReference<AbstractSignal<T>>(newOutput);
		return newOutput;

		//Care is taken, above, not to ever lose the reference to the returned output. This, for example, would allow the reference to be GCd between one line and the other:
		//_output = new WeakReference<AbstractSignal<T>>(new MyOutput());
		//return newOutput.get();
	}

	private AbstractSignal<T> rawOutput() {
		return _output == null ? null : _output.get();
	}

	@Override
	public Consumer<T> setter() {
		return new MySetter();
	}
}
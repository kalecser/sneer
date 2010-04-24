package sneer.bricks.hardware.io.prevalence.state.impl;

import sneer.bricks.hardware.io.prevalence.state.PrevailingState;
import sneer.foundation.lang.Producer;


class PrevailingStateImpl implements PrevailingState {
	
	
	boolean _prevailing;
	
	
	@Override
	public synchronized <T> T produce(Producer<T> producerToEnterPrevalence, final Producer<T> producerToRunInsidePrevalence) {
		if (_prevailing)
			return producerToRunInsidePrevalence.produce();
		
		_prevailing = true;
		try {
			return producerToEnterPrevalence.produce();
		} finally {
			_prevailing = false;
		}
	}	
	
	
	@Override
	public <T> T produce(final Producer<T> producer) {
		Producer<T> SHOULD_NOT_BE_PREVAILING = new Producer<T>() { @Override public T produce() throws RuntimeException {
			throw new IllegalStateException();
		}};
		return produce(producer, SHOULD_NOT_BE_PREVAILING);
	}

	
	@Override
	public boolean isPrevailing() {
		return _prevailing;
	}
	
}

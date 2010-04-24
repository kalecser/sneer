package sneer.bricks.hardware.io.prevalence.nature.impl;

import sneer.bricks.hardware.io.prevalence.state.PrevalenceDispatcher;
import sneer.foundation.lang.Producer;

final class InternalDispatcher implements PrevalenceDispatcher {
	
	@Override
	public synchronized <T> T produce(Producer<T> producerToEnterPrevalence, final Producer<T> producerToRunInsidePrevalence) {
		return producerToRunInsidePrevalence.produce();
	}

	@Override
	public <T> T produce(final Producer<T> producerToEnterPrevalence) {
		throw new IllegalStateException();
	}

	@Override
	public boolean isPrevailing() {
		return true;
	}
}
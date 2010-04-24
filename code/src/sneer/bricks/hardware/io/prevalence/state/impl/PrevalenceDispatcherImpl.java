package sneer.bricks.hardware.io.prevalence.state.impl;

import sneer.bricks.hardware.io.prevalence.state.PrevalenceDispatcher;
import sneer.foundation.lang.Producer;


class PrevalenceDispatcherImpl implements PrevalenceDispatcher {
	
	@Override
	public synchronized <T> T produce(Producer<T> producerForOutsidePrevalence, Producer<T> producerForInsidePrevalence) {
		return producerForOutsidePrevalence.produce();
	}
	
	
	@Override
	public void checkInsidePrevalence() {
		throw new IllegalStateException("Should be running inside the Prevalence environment.");
	}
	
}

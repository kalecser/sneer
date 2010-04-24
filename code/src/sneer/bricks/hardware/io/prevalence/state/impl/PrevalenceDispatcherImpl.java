package sneer.bricks.hardware.io.prevalence.state.impl;

import sneer.bricks.hardware.io.prevalence.state.PrevalenceDispatcher;
import sneer.foundation.lang.Producer;


class PrevalenceDispatcherImpl implements PrevalenceDispatcher {
	
	
	@Override
	public synchronized <T> T produce(Producer<T> producerToEnterPrevalence, final Producer<T> producerToRunInsidePrevalence) {
		return producerToEnterPrevalence.produce();
	}
	
	
	@Override
	public <T> T produceOutsidePrevalence(final Producer<T> producerToEnterPrevalence) {
		return producerToEnterPrevalence.produce();
	}

	
	@Override
	public boolean isPrevailing() {
		return false;
	}
	
}

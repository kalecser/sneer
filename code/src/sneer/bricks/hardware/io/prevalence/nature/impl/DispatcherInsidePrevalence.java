package sneer.bricks.hardware.io.prevalence.nature.impl;

import sneer.bricks.hardware.io.prevalence.state.PrevalenceDispatcher;
import sneer.foundation.lang.Producer;

final class InternalDispatcher implements PrevalenceDispatcher {
	
	@Override
	public synchronized <T> T produce(Producer<T> producerForOutsidePrevalence, final Producer<T> producerForInsidePrevalence) {
		return producerForInsidePrevalence.produce();
	}

	@Override
	public void checkInsidePrevalence() {}
}
package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.foundation.lang.Producer;

final class BrickInstantiation<T> extends BuildingTransaction<T> {
	
	private final Class<T> _brick;
	private final Producer<T> _producer;

	
	BrickInstantiation(Class<T> brick, Producer<T> producer) {
		_brick = brick;
		_producer = producer;
	}


	@Override
	public T produce() {
		T result = _producer.produce();
		my(PrevalentBuilding.class).add(_brick, result);
		return result;
	}
	
}
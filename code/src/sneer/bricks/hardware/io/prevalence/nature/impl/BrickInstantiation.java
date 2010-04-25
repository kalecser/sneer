package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.io.prevalence.map.ExportMap;
import sneer.foundation.lang.Producer;

final class BrickInstantiation<T> extends BuildingTransaction<T> {
	
	private final Class<T> _brick;
	private final Producer<T> _delegate;

	
	BrickInstantiation(Class<T> brick, Producer<T> instantiator) {
		_brick = brick;
		_delegate = instantiator;
	}


	@Override
	public T produce() {
		T brickInstance = _delegate.produce();
		my(ExportMap.class).register(brickInstance);
		T bubble = Bubble.wrap(brickInstance);
		my(PrevalentBuilding.class).add(_brick, bubble);
		return bubble;
	}
	
}
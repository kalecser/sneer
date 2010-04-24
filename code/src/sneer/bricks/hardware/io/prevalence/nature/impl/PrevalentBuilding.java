package sneer.bricks.hardware.io.prevalence.nature.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sneer.foundation.environments.Environment;

class PrevalentBuilding implements Environment {

	private Map<Class<?>, Object> _bricks = new ConcurrentHashMap<Class<?>, Object>();
	
	<T> void add(Class<T> brick, T brickImpl) {
		_bricks.put(brick, brickImpl);
	}

	@Override
	public <T> T provide(Class<T> brick) {
		return (T)_bricks.get(brick);
	}
}

package sneer.bricks.hardware.io.prevalence.nature.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.hardware.io.prevalence.flag.PrevalenceFlag;
import sneer.foundation.environments.Environment;

class PrevalentBuilding implements Environment {

	
	private Map<Class<?>, Object> _bricks = new ConcurrentHashMap<Class<?>, Object>();
	
	
	{
		_bricks.put(PrevalenceFlag.class, new PrevalenceFlag() { @Override public boolean isInsidePrevalence() {
			return true;
		}});
	}
	
	
	<T> void add(Class<T> brick, T brickImpl) {
		_bricks.put(brick, brickImpl);
	}

	
	@Override
	public <T> T provide(Class<T> brick) {
		if (brick == PrevalentBuilding.class)
			return (T)this;
		
		return (T)_bricks.get(brick);
	}
}

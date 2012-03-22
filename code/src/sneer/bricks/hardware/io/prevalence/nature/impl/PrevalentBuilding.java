package sneer.bricks.hardware.io.prevalence.nature.impl;

import static basis.environments.Environments.my;
import basis.lang.CacheMap;
import basis.lang.Producer;
import sneer.bricks.hardware.cpu.threads.Threads;

class PrevalentBuilding {

	
	private CacheMap<Class<?>, Object> _bricks = CacheMap.newInstance();
	
	
	<T> T get(Class<T> brick) {
		return (T)_bricks.get(brick);
	}

	
	synchronized
	<T> T get(Class<T> brick, Producer<T> producerToUseIfAbsent) {
		this.notifyAll();
		return (T)_bricks.get(brick, (Producer<Object>)producerToUseIfAbsent);
	}
	
	
	synchronized
	<T> T waitForInstance(Class<T> brick) {
		while (get(brick) == null)
			my(Threads.class).waitWithoutInterruptions(this);
			
		return get(brick);
	}
	
}

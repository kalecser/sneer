package sneer.bricks.hardware.io.prevalence.nature.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sneer.foundation.environments.Environment;

class PrevalentBuilding implements Environment {

	private Map<Class<?>, Object> _bricks = new ConcurrentHashMap<Class<?>, Object>();
	private Map<Object, Long> _idsByObject = new ConcurrentHashMap<Object, Long>();
	private Map<Long, Object> _objectsById = new ConcurrentHashMap<Long, Object>();
	private long _nextId;
	
	public <T> void add(Class<T> brick, T brickImpl) {
		_bricks.put(brick, brickImpl);
	}

	public <T> T brick(Class<T> brick) {
		return (T)_bricks.get(brick);
	}

	@Override
	public <T> T provide(Class<T> intrface) {
		return brick(intrface);
	}

	public long idFor(Object object) {
		Long existing = _idsByObject.get(object);
		if (null != existing)
			return existing;
		
		long id = nextId();
		_idsByObject.put(object, id);
		_objectsById.put(id, object);
		return id;
	}

	private long nextId() {
		return _nextId++;
	}

	public Object objectById(long id) {
		return _objectsById.get(id);
	}
	
}

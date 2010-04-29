package sneer.bricks.hardware.io.prevalence.map.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.hardware.io.prevalence.flag.PrevalenceFlag;
import sneer.bricks.hardware.io.prevalence.map.ExportMap;

class ExportMapImpl implements ExportMap {
	
	private Map<Object, Long> _idsByObject = new ConcurrentHashMap<Object, Long>();
	private Map<Long, Object> _objectsById = new ConcurrentHashMap<Long, Object>();
	private long _nextId = 1;

	
	@Override
	public <T> T register(T object) {
		checkInsidePrevalence(object);
		
		if (_idsByObject.containsKey(object))
			throw new IllegalStateException();
		
		long id = _nextId++;
		_idsByObject.put(object, id);
		_objectsById.put(id, object);
		return object;
	}

	
	@Override
	public boolean isRegistered(Object object) {
		return _idsByObject.containsKey(object);
	}

	
	private <T> void checkInsidePrevalence(T object) {
		if (!my(PrevalenceFlag.class).isInsidePrevalence())
			throw new IllegalStateException("Trying to register object '" + object + "' outside prevalent environment.");
	}

	
	@Override
	public void marshal(Object[] args) {
		if (args == null)
			return;
		
		for (int i = 0; i < args.length; i++)
			args[i] = marshalIfNecessary(args[i]);
	}
	
	
	private Object marshalIfNecessary(Object object) {
		if (object == null) return null;
		
		Long id = _idsByObject.get(object);
		return id == null
			? object
			: new OID(id);
	}


	@Override
	public long marshal(Object object) {
		Long result = _idsByObject.get(object);
		if (result == null)
			throw new IllegalStateException("Id not found for object: " + object);
		return result;
	}
	
	
	@Override
	public void unmarshal(Object[] array) {
		if (array == null)
			return;
		
		for (int i = 0; i < array.length; i++)
			array[i] = unmarshal(array[i]);
	}

	
	private Object unmarshal(Object object) {
		return object instanceof OID
			? unmarshal(((OID)object)._id)
			: object;
	}

	
	@Override
	public Object unmarshal(long id) {
		Object result = _objectsById.get(id);
		if (result == null)
			throw new IllegalStateException("Object not found for id: " + id);
		return result;
	}
	
}

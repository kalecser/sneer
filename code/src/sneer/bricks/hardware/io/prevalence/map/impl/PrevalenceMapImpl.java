package sneer.bricks.hardware.io.prevalence.map.impl;

import static basis.environments.Environments.my;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import basis.lang.Immutable;

import sneer.bricks.hardware.io.prevalence.flag.PrevalenceFlag;
import sneer.bricks.hardware.io.prevalence.map.PrevalenceMap;

class PrevalenceMapImpl implements PrevalenceMap {
	
	private Map<Object, Long> _idsByObject = new ConcurrentHashMap<Object, Long>();
	private Map<Long, Object> _objectsById = new ConcurrentHashMap<Long, Object>();
	private long _nextId = 1;

	
	@Override
	public void register(Object object) {
		checkInsidePrevalence(object);
		
		if (_idsByObject.containsKey(object))
			throw new IllegalStateException("Object already registered in prevalence map: " + object);
		
		long id = _nextId++;
		_idsByObject.put(object, id);
		_objectsById.put(id, object);
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
	public Object[] marshal(Object[] array) {
		if (array == null)
			return null;
		
		Object[] result = new Object[array.length]; 
		for (int i = 0; i < result.length; i++)
			result[i] = marshalIfNecessary(array[i]);
		return result;
	}
	
	
	private Object marshalIfNecessary(Object object) {
		if (object == null) return null;
		
		Long id = _idsByObject.get(object);
		if (id != null)
			return new OID(id);
		
		if (requiresRegistration(object))
			throw new IllegalStateException("Mutable object " + object + " should have been registered in prevalence map.");
		return object;
	}


	@Override
	public long marshal(Object object) {
		Long result = _idsByObject.get(object);
		if (result == null)
			throw new IllegalStateException("Id not found for object: " + object);
		return result;
	}
	
	
	@Override
	public Object[] unmarshal(Object[] array) {
		if (array == null)
			return null;

		Object[] result = new Object[array.length]; 
		for (int i = 0; i < array.length; i++)
			result[i] = unmarshal(array[i]);
		return result;
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


	@Override
	public boolean requiresRegistration(Object object) {
		if (object == null) return false;

		Class<?> type = object.getClass();
		if (type.isArray()) return false;
		if (Collection.class.isAssignableFrom(type)) return false;
		if (Immutable.isImmutable(type)) return false;
		
		return true;
	}
	
}

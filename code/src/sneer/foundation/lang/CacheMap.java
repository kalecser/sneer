package sneer.foundation.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class CacheMap<K, V> extends ConcurrentHashMap<K, V> {

	static public <K, V> CacheMap<K, V> newInstance() {
		return new CacheMap<K, V>();
	}
	
	
	Map<K, Thread> _keysByResolver = new HashMap<K, Thread>();
	
	
	public V get(K key, final Producer<V> producerToUseIfAbsent) {
		return get(key, new Functor<K, V>() { @Override public V evaluate(K ignored) {
			return producerToUseIfAbsent.produce();
		}});
	}

	
	public V get(K key, Functor<K, V> functorToUseIfAbsent) {
		boolean thisThreadMustResolve = false;
		synchronized (_keysByResolver) {
			V found = get(key);
			if (found != null) return found;
			
			thisThreadMustResolve = volunteerToResolve(key);
		}

		if (thisThreadMustResolve) {
			V resolved = functorToUseIfAbsent.evaluate(key);
			synchronized (_keysByResolver) {
				put(key, resolved);
				_keysByResolver.remove(key);
				_keysByResolver.notifyAll();
			};
			return resolved;
		}
		
		synchronized (_keysByResolver) {
			while (_keysByResolver.containsKey(key))
				waitWithoutInterruptions();
		}
		
		return get(key);
	}


	private boolean volunteerToResolve(K key) {
		Thread resolver = _keysByResolver.get(key);
		
		if (resolver == null) {
			_keysByResolver.put(key, Thread.currentThread());
			return true;
		}

		if (resolver == Thread.currentThread())
			throw new IllegalStateException("The resolution (loading) of " + key + " is being triggered recursively.");
		
		return false;
	}

	
	private void waitWithoutInterruptions() {
		try {
			_keysByResolver.wait();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

}

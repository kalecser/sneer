package sneer.foundation.lang;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class CacheMap<K, V> extends ConcurrentHashMap<K, V> {

	static public <K, V> CacheMap<K, V> newInstance() {
		return new CacheMap<K, V>();
	}
	
	
	Set<K> _keysBeingResolved = new HashSet<K>();
	
	
	public V get(K key, final Producer<V> producerToUseIfAbsent) {
		return get(key, new Functor<K, V>() { @Override public V evaluate(K ignored) {
			return producerToUseIfAbsent.produce();
		}});
	}

	
	public V get(K key, Functor<K, V> functorToUseIfAbsent) {
		boolean mustResolve;
		synchronized (_keysBeingResolved) {
			V present = get(key);
			if (present != null) return present;
			
			mustResolve = _keysBeingResolved.add(key);
		}

		if (mustResolve) {
			V resolved = functorToUseIfAbsent.evaluate(key);
			synchronized (_keysBeingResolved) {
				put(key, resolved);
				_keysBeingResolved.remove(key);
				_keysBeingResolved.notifyAll();
			};
			return resolved;
		}
		
		synchronized (_keysBeingResolved) {
			while (_keysBeingResolved.contains(key))
				waitWithoutInterruptions();
		}
		
		return get(key);
	}

	
	private void waitWithoutInterruptions() {
		try {
			_keysBeingResolved.wait();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

}

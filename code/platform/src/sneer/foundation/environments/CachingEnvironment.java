package sneer.foundation.environments;

import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Functor;



public class CachingEnvironment implements Environment {

	public CachingEnvironment(Environment delegate) {
		_delegate = delegate;
	}

	
	private final CacheMap<Class<?>, Object> _cache = CacheMap.newInstance();
	
	private final Environment _delegate;

	private Functor<Class<?>, Object> _functor = new Functor<Class<?>, Object>(){ @Override public Object evaluate(Class<?> key) {
		return _delegate.provide(key);
	}};;

	
	@Override
	public <T> T provide(Class<T> need) {
		return (T)_cache.get(need, _functor);
	}

	
	public void clear() {
		_cache.clear();
	}

}

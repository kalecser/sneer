package sneer.foundation.environments;

import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Functor;



public class CachingEnvironment implements Environment {

	private final CacheMap<Class<?>, Object> _cache = new CacheMap<Class<?>, Object>();
	
	private final Environment _delegate;

	private Functor<Class<?>, Object> _functor = new Functor<Class<?>, Object>(){ @Override public Object evaluate(Class<?> key) {
		return _delegate.provide(key);
	}};;

	public CachingEnvironment(Environment delegate) {
		_delegate = delegate;
	}

	@Override
	public <T> T provide(Class<T> need) {
		return (T)_cache.get(need, _functor);
	}

	public void clear() {
		_cache.clear();
	}

}

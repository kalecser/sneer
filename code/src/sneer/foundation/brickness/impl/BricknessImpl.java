package sneer.foundation.brickness.impl;

import java.lang.reflect.Constructor;
import java.util.List;

import sneer.foundation.brickness.Nature;
import sneer.foundation.environments.Bindings;
import sneer.foundation.environments.CachingEnvironment;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.EnvironmentUtils;
import sneer.foundation.lang.ByRef;
import sneer.foundation.lang.Producer;


public class BricknessImpl implements Environment {
	
	private static final class BrickImplProducer<T> implements Producer<T> {
		private final Class<T> _brickImpl;

		private BrickImplProducer(Class<T> brickImpl) {
			_brickImpl = brickImpl;
		}

		@Override public T produce() throws RuntimeException {
			return (T)newInstance(_brickImpl);
		}
	}

	public BricknessImpl(Object... bindings) {
		
		_brickImplLoader = new BrickImplLoader(_apiClassLoader);
		
		_bindings = new Bindings();
		_bindings.bind(this);
		_bindings.bind(new BrickSerializationMapperImpl(_apiClassLoader, _brickImplLoader));
		_bindings.bind(bindings);
	
		_cache = createCachingEnvironment();
	}
	
	private final Bindings _bindings;
	private CachingEnvironment _cache;
	private final BrickImplLoader _brickImplLoader;
	private final ByRef<ClassLoader> _apiClassLoader = ByRef.newInstance();
	
	@Override
	public <T> T provide(Class<T> intrface) {
		return _cache.provide(intrface);
	}

	private CachingEnvironment createCachingEnvironment() {
		return new CachingEnvironment(EnvironmentUtils.compose(_bindings.environment(), new Environment(){ @Override public <T> T provide(Class<T> brick) {
			return loadBrick(brick);
		}}));
	}
	
	private <T> T loadBrick(Class<T> brick) {
		try {
			return tryToLoadBrick(brick);
		} catch (ClassNotFoundException e) {
			throw new BrickLoadingException("Exception loading brick: " + brick + ": " + e.getMessage(), e);
		}
	}

	private <T> T tryToLoadBrick(Class<T> brick) throws ClassNotFoundException {
		checkClassLoader(brick);
		
		Class<T> brickImpl = _brickImplLoader.loadImplClassFor(brick);
		return instantiate(brick, brickImpl);
	}
	
	private <T> T instantiate(Class<T> brick, final Class<T> brickImpl) {
		List<Nature> natures = _brickImplLoader.naturesFor(brick);
		if (natures.isEmpty())
			return (T)newInstance(brickImpl);
		
		Nature nature = natures.get(0);
		return nature.instantiate(brick, brickImpl, new BrickImplProducer<T>(brickImpl));
	}

	private static <T> T newInstance(Class<?> brickImpl) {
		try {
			Constructor<?> constructor = brickImpl.getDeclaredConstructor();
			constructor.setAccessible(true);
			return (T)constructor.newInstance();
		} catch (Exception e) {
			throw new BrickLoadingException(e);
		}
	}

	private void checkClassLoader(Class<?> brick) {
		if (_apiClassLoader.value == null)
			_apiClassLoader.value = brick.getClassLoader();
		
		if (brick.getClassLoader() != _apiClassLoader.value)
			throw new IllegalStateException("" + brick + " was loaded with " + brick.getClassLoader() + " instead of " + _apiClassLoader.value + " like previous bricks.");
	}
}

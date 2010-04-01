package sneer.foundation.brickness.impl;

import static sneer.foundation.environments.Environments.my;

import java.lang.reflect.Constructor;
import java.util.List;

import sneer.foundation.brickness.BrickLoadingException;
import sneer.foundation.brickness.Nature;
import sneer.foundation.environments.Bindings;
import sneer.foundation.environments.CachingEnvironment;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.EnvironmentUtils;
import sneer.foundation.lang.Producer;


public class BricknessImpl implements Environment {
	
<<<<<<< Updated upstream:code/src/sneer/foundation/brickness/impl/BricknessImpl.java
=======
	private static final class BrickImplProducer<T> implements Producer<T> {
		private final Class<T> _brickImpl;

		private BrickImplProducer(Class<T> brickImpl) {
			_brickImpl = brickImpl;
		}

		@Override public T produce() throws RuntimeException {
			return (T)newInstance(_brickImpl);
		}
	}

>>>>>>> Stashed changes:code/src/sneer/foundation/brickness/impl/BricknessImpl.java
	public BricknessImpl(Object... bindings) {
		_bindings = new Bindings();
		_bindings.bind(this);
		_bindings.bind(bindings);
	
		_cache = createCachingEnvironment();
		
		_brickImplLoader = new BrickImplLoader();
	}
<<<<<<< Updated upstream:code/src/sneer/foundation/brickness/impl/BricknessImpl.java

=======
>>>>>>> Stashed changes:code/src/sneer/foundation/brickness/impl/BricknessImpl.java
	
	private final Bindings _bindings;
	private CachingEnvironment _cache;
	private final BrickImplLoader _brickImplLoader;
	private ClassLoader _classLoader;
<<<<<<< Updated upstream:code/src/sneer/foundation/brickness/impl/BricknessImpl.java

=======
>>>>>>> Stashed changes:code/src/sneer/foundation/brickness/impl/BricknessImpl.java
	
	@Override
	public <T> T provide(Class<T> intrface) {
		if (my(Environment.class) == null) throw new IllegalStateException("provide() cannot be called outside an environment."); //Delete this line after July 2009 if the exception is never thrown.
		
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
		List<Nature> natures = BrickImplLoader.naturesFor(brick);
		if (natures.isEmpty())
			return (T)newInstance(brickImpl);
		
		Nature nature = natures.get(0);
<<<<<<< Updated upstream:code/src/sneer/foundation/brickness/impl/BricknessImpl.java
		return nature.instantiate(brick, brickImpl, new Producer<T>() { @Override public T produce() throws RuntimeException {
			return (T)newInstance(brickImpl);
		}});
	}

	private <T> T newInstance(Class<?> brickImpl) {
=======
		return nature.instantiate(brick, brickImpl, new BrickImplProducer<T>(brickImpl));
	}

	private static <T> T newInstance(Class<?> brickImpl) {
>>>>>>> Stashed changes:code/src/sneer/foundation/brickness/impl/BricknessImpl.java
		try {
			Constructor<?> constructor = brickImpl.getDeclaredConstructor();
			constructor.setAccessible(true);
			return (T)constructor.newInstance();
		} catch (Exception e) {
			throw new BrickLoadingException(e);
		}
	}

	private void checkClassLoader(Class<?> brick) {
		if (_classLoader == null)
			_classLoader = brick.getClassLoader();
		
		if (brick.getClassLoader() != _classLoader)
			throw new IllegalStateException("" + brick + " was loaded with " + brick.getClassLoader() + " instead of " + _classLoader + " like previous bricks.");
	}



}


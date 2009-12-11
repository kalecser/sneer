package sneer.foundation.brickness.impl;

import static sneer.foundation.environments.Environments.my;

import java.lang.reflect.Constructor;
import java.util.List;

import sneer.foundation.brickness.BrickLoadingException;
import sneer.foundation.brickness.Nature;
import sneer.foundation.brickness.RuntimeNature;
import sneer.foundation.environments.Bindings;
import sneer.foundation.environments.CachingEnvironment;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.EnvironmentUtils;
import sneer.foundation.lang.Producer;
import sneer.foundation.lang.exceptions.NotImplementedYet;


public class BricknessImpl implements Environment {
	
	public BricknessImpl(Object... bindings) {
		_bindings = new Bindings();
		_bindings.bind(this);
		_bindings.bind(bindings);
	
		_cache = createCachingEnvironment();
		
		_brickImplLoader = new BrickImplLoader();
	}

	
	private final Bindings _bindings;
	private CachingEnvironment _cache;
	private final BrickImplLoader _brickImplLoader;
	private ClassLoader _classLoader;

	
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
		
		Class<?> brickImpl = _brickImplLoader.loadImplClassFor(brick);
		return instantiate(brick, brickImpl);
	}
	
	private <T> T instantiate(Class<T> brick, final Class<?> brickImpl) {
		
		RuntimeNature runtimeNature = firstRuntimeNatureOf(brick);
		if (runtimeNature != null)
			return runtimeNature.instantiate(brick, brickImpl, new Producer<T>() { @Override public T produce() throws RuntimeException {
				return (T)newInstance(brickImpl);
			}});
		
		return (T)newInstance(brickImpl);
	}


	private RuntimeNature firstRuntimeNatureOf(Class<?> brick) {
		List<Nature> natures = BrickImplLoader.naturesFor(brick);
		
		RuntimeNature found = null;
		for (Nature nature : natures) {
			if (nature instanceof RuntimeNature) {
				if (null != found)
					throw new NotImplementedYet("Multiple runtime natures");
				found = (RuntimeNature) nature;
			}
		}
		return found;
	}


	private <T> T newInstance(Class<?> brickImpl) {
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


package sneer.bricks.software.bricks.snapploader.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.software.bricks.finder.BrickFinder;
import sneer.bricks.software.bricks.snapploader.Snapp;
import sneer.bricks.software.bricks.snapploader.SnappLoader;
import sneer.foundation.lang.Closure;
import sneer.foundation.util.concurrent.Latch;

class SnappLoaderImpl implements SnappLoader {

	private final ClassLoader _apiClassLoader = SnappLoader.class.getClassLoader();
	private final Collection<Object> _refToAvoidGC = Collections.synchronizedSet(new HashSet<Object>());

	private final Latch _loadingFinished = new Latch();
	private boolean _wereThrowablesCaughtWhenLoadingSnapps;

	
	{
		my(Threads.class).startDaemon("SnappLoader", new Closure() {  @Override public void run() {
			loadAllSnapps();
		}});
	}
	
	
	@Override
	public Latch loadingFinished() {
		return _loadingFinished;
	}
	
	
	@Override
	public boolean wereThrowablesCaughtWhenLoadingSnapps() {
		return _wereThrowablesCaughtWhenLoadingSnapps;
	}

	
	private void loadAllSnapps() {
		for (String brickName : allBrickNames())
			loadIfSnapp(brickName);
		
		_loadingFinished.open();
	}


	private void loadIfSnapp(String brickName) {
		try {
			Class<?> brick = _apiClassLoader.loadClass(brickName);
			if (isSnapp(brick)) startAndKeep(brick);
		} catch (Throwable t) {
			_wereThrowablesCaughtWhenLoadingSnapps = true;
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error loading Brick " + brickName, "If this error doesn't go away by itself, get an expert sovereign friend to help you.", t);
		}
	}


	private Collection<String> allBrickNames() {
		try {
			return my(BrickFinder.class).findBricks();
		} catch (Throwable t) {
			_wereThrowablesCaughtWhenLoadingSnapps = true;
			throw new IllegalStateException(t);
		}
	}

	
	private void startAndKeep(final Class<?> brick) {
		_refToAvoidGC.add(my(brick));
	}

	
	private boolean isSnapp(Class<?> brick) {
		return brick.getAnnotation(Snapp.class) != null;
	}

}

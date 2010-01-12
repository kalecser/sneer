package sneer.bricks.software.bricks.snappstarter.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import sneer.bricks.pulp.exceptionhandling.ExceptionHandler;
import sneer.bricks.software.bricks.finder.BrickFinder;
import sneer.bricks.software.bricks.snappstarter.SnappStarter;
import sneer.foundation.brickness.Brick;
import sneer.foundation.brickness.Nature;

class SnappStarterImpl implements SnappStarter {

	private final ClassLoader _apiClassLoader = SnappStarter.class.getClassLoader();
	private final Collection<Object> _referenceToAvoidGC = Collections.synchronizedSet(new HashSet<Object>());

	@Override
	public void startSnapps() {
		try {
			tryToStartSnapps();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private void tryToStartSnapps() throws IOException, ClassNotFoundException {
		for (String brickName : my(BrickFinder.class).findBricks()) {
			Class<?> brick = _apiClassLoader.loadClass(brickName);
			if (isSnapp(brick)) startAndKeep(brick);
		}
	}

	private void startAndKeep(final Class<?> brick) {
//		my(Threads.class).startDaemon("Starting Snapp: " + brick.getName(), new Runnable() { @Override public void run() {
//			...		
//		}});
		
		my(ExceptionHandler.class).shield(new Runnable() { @Override public void run() {
			_referenceToAvoidGC.add(my(brick));
		}});
	}

	private boolean isSnapp(Class<?> brick) {
		return !hasMethods(brick) && hasImpl(brick) && !isNature(brick);
	}

	private boolean hasMethods(Class<?> brick) {
		return brick.getDeclaredMethods().length > 0;
	}

	private boolean isNature(Class<?> brick) {
		return Nature.class.isAssignableFrom(brick);
	}

	private boolean hasImpl(Class<?> brick) {
		return brick.getAnnotation(Brick.class).hasImpl();
	}

}

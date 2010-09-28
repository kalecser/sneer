package sneer.bricks.pulp.exceptionhandling.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.io.log.exceptions.ExceptionLogger;
import sneer.bricks.pulp.exceptionhandling.ExceptionHandler;
import sneer.foundation.lang.ClosureX;

class ExceptionHandlerImpl implements ExceptionHandler {

	@Override
	public void shield(Runnable runnable) {
		try {
			runnable.run();
		} catch (ThreadDeath t) {
			throw t;
		} catch (Throwable t) {
			my(ExceptionLogger.class).log(t, "Exception shielded.");
		}
	}

	@Override
	public void shieldX(final ClosureX<?> closure) {
		try {
			closure.run();
		} catch (ThreadDeath t) {
			throw t;
		} catch (Throwable t) {
			my(ExceptionLogger.class).log(t, "Exception shielded.");
		}
	}

}
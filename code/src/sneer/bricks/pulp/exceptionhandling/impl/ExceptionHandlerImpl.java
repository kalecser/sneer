package sneer.bricks.pulp.exceptionhandling.impl;

import static basis.environments.Environments.my;
import basis.lang.ClosureX;
import sneer.bricks.hardware.io.log.exceptions.ExceptionLogger;
import sneer.bricks.pulp.exceptionhandling.ExceptionHandler;

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
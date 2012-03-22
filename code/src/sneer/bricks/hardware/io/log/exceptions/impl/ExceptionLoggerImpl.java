package sneer.bricks.hardware.io.log.exceptions.impl;

import static basis.environments.Environments.my;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.io.log.exceptions.ExceptionLogger;
import sneer.bricks.hardware.io.log.exceptions.robust.RobustExceptionLogging;
import sneer.bricks.hardware.io.log.stacktrace.StackTraceLogger;


class ExceptionLoggerImpl implements ExceptionLogger {

	@Override
	public void log(Throwable throwable) {
		log(throwable, "{} thrown.", throwable.getClass());
	}

	
	@Override
	public void log(Throwable throwable, String message, Object... messageInsets) {
		message += "\n" + my(StackTraceLogger.class).stackTrace(throwable);
		my(Logger.class).log(message, messageInsets);
		leakIfNecessary(throwable);
	}

	
	private void leakIfNecessary(Throwable throwable) {
		if (my(RobustExceptionLogging.class).isOn()) return;
		
		if (throwable instanceof RuntimeException) throw (RuntimeException)throwable;
		if (throwable instanceof Error) throw (Error)throwable;
		throw new RuntimeException("Throwable leaked by ExceptionLogger", throwable);
	}
	
}

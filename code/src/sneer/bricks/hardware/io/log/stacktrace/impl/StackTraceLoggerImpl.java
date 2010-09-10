package sneer.bricks.hardware.io.log.stacktrace.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.io.log.stacktrace.StackTraceLogger;

class StackTraceLoggerImpl implements StackTraceLogger{

	@Override
	public String stackTrace() {
		class StackTrace extends RuntimeException{};
		return stackTrace(new StackTrace());
	}

	
	@Override
	public void logStackTrace() {
		my(Logger.class).log(stackTrace());
	}

	
	@Override
	public String stackTrace(Throwable throwable) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter _log = new PrintWriter(out, true);
		throwable.printStackTrace(_log);
		return new String(out.toByteArray());
	}
}

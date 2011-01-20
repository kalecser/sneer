package sneer.bricks.hardware.io.log.stacktrace.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.io.log.stacktrace.StackTraceLogger;

class StackTraceLoggerImpl implements StackTraceLogger{

	@Override
	public String stackTrace() {
		return stackTrace(Thread.currentThread());
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


	@Override
	public void logStackTrace(Thread thread, String message, Object... insets) {
		my(Logger.class).log(message + "\n" + report(thread), insets);
	}


	private String report(Thread thread) {
		return "Thread state: " + thread.getState() + stackTrace(thread); 
	}


	private String stackTrace(Thread thread) {
		String result = "";
		for (StackTraceElement element : thread.getStackTrace())
			result += "\n\t" + element;
		return result;
	}
	
}

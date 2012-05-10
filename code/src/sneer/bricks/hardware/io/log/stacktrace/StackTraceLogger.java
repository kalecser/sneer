package sneer.bricks.hardware.io.log.stacktrace;

import basis.brickness.Brick;

@Brick
public interface StackTraceLogger {

	void logStackTrace();
	String stackTrace();
	String stackTrace(Thread thread);
	String stackTrace(Throwable throwable);
	void logStackTrace(Thread thread, String message, Object... insets);

}

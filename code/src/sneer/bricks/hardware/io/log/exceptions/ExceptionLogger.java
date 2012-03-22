package sneer.bricks.hardware.io.log.exceptions;

import basis.brickness.Brick;

@Brick
public interface ExceptionLogger {
	
	void log(Throwable throwable);
	void log(Throwable throwable, String message, Object... messageInsets);
	
}

package sneer.bricks.hardware.io.log;

import basis.brickness.Brick;

@Brick
public interface Logger {

	void log(String message, Object... messageInsets);

}

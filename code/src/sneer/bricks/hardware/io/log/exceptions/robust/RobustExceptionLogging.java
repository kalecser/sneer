package sneer.bricks.hardware.io.log.exceptions.robust;

import basis.brickness.Brick;

@Brick
public interface RobustExceptionLogging {

	void turnOn();
	boolean isOn();
	
}

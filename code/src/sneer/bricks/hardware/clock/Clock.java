package sneer.bricks.hardware.clock;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface Clock {
	
	Signal<Long> time();
	
	/** Returns System.currentTimeMillis() if not in test mode */
	long preciseTime();
	
	void advanceTime(long deltaMillis);
	void advanceTimeTo(long absoluteTimeMillis);

}

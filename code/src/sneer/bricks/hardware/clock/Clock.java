package sneer.bricks.hardware.clock;

import basis.brickness.Brick;
import sneer.bricks.pulp.reactive.Signal;

@Brick
public interface Clock {
	
	Signal<Long> time();
	void advanceTime(long deltaMillis);
	void advanceTimeTo(long absoluteTimeMillis);

}

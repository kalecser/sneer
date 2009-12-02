package sneer.bricks.hardware.clock.ticker.custom;

import sneer.foundation.brickness.Brick;

@Brick
public interface CustomClockTicker {

	void start(int millisToSleep);

	void start(int millisToSleep, float acceleration);

}

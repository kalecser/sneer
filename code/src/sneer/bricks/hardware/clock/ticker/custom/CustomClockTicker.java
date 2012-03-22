package sneer.bricks.hardware.clock.ticker.custom;

import basis.brickness.Brick;

@Brick
public interface CustomClockTicker {

	void start(int millisToSleep);

	void start(int millisToSleep, long timeIncrement);

}

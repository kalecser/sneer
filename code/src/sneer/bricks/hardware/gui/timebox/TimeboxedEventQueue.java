package sneer.bricks.hardware.gui.timebox;

import basis.brickness.Brick;

@Brick
public interface TimeboxedEventQueue {

	void startQueueing(int timeboxDuration);
	void stopQueueing();

}

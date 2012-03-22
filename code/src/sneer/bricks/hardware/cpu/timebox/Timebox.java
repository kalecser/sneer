package sneer.bricks.hardware.cpu.timebox;

import basis.brickness.Brick;
import basis.lang.Closure;

@Brick
public interface Timebox {

	/**@param toCallWhenBlocked called when toRun is blocked waiting for a synchronization monitor and cannot be stopped.*/
	void run(int durationInMillis, Runnable toRun, Runnable toCallWhenBlocked);

	Closure prepare(int durationInMillis, Runnable toRun, Runnable toCallWhenBlocked);


}

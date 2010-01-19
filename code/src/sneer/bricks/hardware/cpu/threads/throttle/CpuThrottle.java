package sneer.bricks.hardware.cpu.threads.throttle;

import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.ClosureX;

@Brick
public interface CpuThrottle {

	/** Sets the maximum CPU usage percentage for the current thread, calls closure.run() and resets the maximum CPU usage percentage to what it was before. See Threads.startStepping(Runnable). */
	<X extends Throwable> void limitMaxCpuUsage(int percentage, ClosureX<X> closure) throws X;

	/** Sleeps for a while if necessary so that this thread does not exceed its max CPU usage limit. */
	void yield();
	
}

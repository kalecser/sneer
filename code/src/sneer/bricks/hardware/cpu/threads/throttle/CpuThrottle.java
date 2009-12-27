package sneer.bricks.hardware.cpu.threads.throttle;

import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.ClosureX;

@Brick
public interface CpuThrottle {

	/** Sets the maximum CPU usage percentage for the current thread, calls context.produce() and resets the maximum CPU usage percentage to what it was before. See Threads.startStepping(Runnable). */
	<X extends Throwable> void limitMaxCpuUsage(int percentage, ClosureX<X> context) throws X;

	/** Returns the max CPU usage percentage set for the current thread. Returns 100 if no value is set. See Threads.startStepping(Runnable). */
	int maxCpuUsage();
	
}

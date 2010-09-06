package sneer.bricks.hardware.cpu.threads;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.pulp.events.pulsers.PulseSource;
import sneer.foundation.brickness.Brick;

@Brick
public interface Threads {

	/** A new daemon will be started to continuously call steppable.run() until the returned Contract's dispose() method is called. If the CpuThrottle.maxCpuUsage() is set for the calling thread, the stepping daemon will use the CPU only up to that specified percentage. This is implemented by simply measuring how long each step takes and waiting a proportional period between each step. */
	Contract startStepping(Runnable steppable);
	Contract startStepping(String threadName, Runnable notifier);
	
	void startDaemon(String threadName, Runnable runnable);

	void sleepWithoutInterruptions(long milliseconds);
	void waitWithoutInterruptions(Object object);
	void joinWithoutInterruptions(Thread thread);

	/** Waits until the crashAllThreads method is called.*/
	void waitUntilCrash();
	void crashAllThreads();
	PulseSource crashed();


}

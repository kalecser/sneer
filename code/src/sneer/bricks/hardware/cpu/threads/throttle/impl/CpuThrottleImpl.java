package sneer.bricks.hardware.cpu.threads.throttle.impl;

import sneer.bricks.hardware.cpu.threads.throttle.CpuThrottle;
import sneer.foundation.lang.ClosureX;

public class CpuThrottleImpl implements CpuThrottle {

	private static final ThreadLocal<Throttle> _throttleByThread = new ThreadLocal<Throttle>();

	
	@Override
	public <X extends Throwable> void limitMaxCpuUsage(int percentage, ClosureX<X> closure) throws X {
		Throttle previous = _throttleByThread.get();
		_throttleByThread.set(new Throttle(percentage));
		try {
			closure.run();
		} finally {
			_throttleByThread.set(previous);
		}
	}
	
	
	@Override
	public void yield() {
		Throttle throttle = _throttleByThread.get();
		if (throttle == null) return; // Fix: Using Stepper this will always be the case (the CPU usage was set in a different thread)
		
		throttle.yield();
	}

}

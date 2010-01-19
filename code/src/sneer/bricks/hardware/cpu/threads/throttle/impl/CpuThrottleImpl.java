package sneer.bricks.hardware.cpu.threads.throttle.impl;

import sneer.bricks.hardware.cpu.threads.throttle.CpuThrottle;
import sneer.foundation.lang.ClosureX;

public class CpuThrottleImpl implements CpuThrottle {

	private final ThreadLocal<Throttle> _throttleByThread = new ThreadLocal<Throttle>();

	
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
		if (throttle == null) return;
		
		throttle.yield();
	}

}

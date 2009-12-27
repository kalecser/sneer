package sneer.bricks.hardware.cpu.threads.throttle.impl;

import sneer.bricks.hardware.cpu.threads.throttle.CpuThrottle;
import sneer.foundation.lang.ClosureX;

public class CpuThrottleImpl implements CpuThrottle {

	private final ThreadLocal<Integer> _maxCpuUsage = new ThreadLocal<Integer>();

	private void setMaxCpuUsage(int percentage) {
		if (percentage < 1  || percentage > 100)
			throw new IllegalArgumentException("Parameter must be an integer between 1 and 100");
		_maxCpuUsage.set(percentage);
	}


	@Override
	public int maxCpuUsage() {
		Integer result = _maxCpuUsage.get();
		return (result == null) ? 100 : result;
	}


	@Override
	public <X extends Throwable> void limitMaxCpuUsage(int percentage, ClosureX<X> context) throws X {
		int previous = maxCpuUsage();
		setMaxCpuUsage(percentage);
		try {
			context.run();
		} finally {
			setMaxCpuUsage(previous);
		}
	}


}

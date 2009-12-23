package sneer.bricks.hardware.cpu.threads.throttle.impl;

import sneer.bricks.hardware.cpu.threads.throttle.CpuThrottle;
import sneer.foundation.lang.ProducerWithThrowable;

public class CpuThrottleImpl implements CpuThrottle {

	private final ThreadLocal<Integer> _maxCpuUsage = new ThreadLocal<Integer>();

	private void setMaxCpuUsage(int percentage) {
		if (percentage < 1  ) throw new IllegalArgumentException();
		if (percentage > 100) throw new IllegalArgumentException();
		_maxCpuUsage.set(percentage);
	}
	

	@Override
	public int maxCpuUsage() {
		Integer result = _maxCpuUsage.get();
		return result == null
			? 100
			: result;
	}

	@Override
	public <T, X extends Throwable> T limitMaxCpuUsage(int percentage, ProducerWithThrowable<T, X> context) throws X {
		int previous = maxCpuUsage();
		setMaxCpuUsage(percentage);
		try {
			return context.produce();
		} finally {
			setMaxCpuUsage(previous);
		}
	}

}

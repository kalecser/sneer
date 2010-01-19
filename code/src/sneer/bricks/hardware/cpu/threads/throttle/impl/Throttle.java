package sneer.bricks.hardware.cpu.threads.throttle.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.threads.Threads;


class Throttle {

	private final float _fractionToYield;
	private final long _t0;

	private long _waited;
	private float _millisToWait = 1;

	
	Throttle(int percentage) {
		if (percentage < 1  || percentage > 100)
			throw new IllegalArgumentException("Parameter must be an integer between 1 and 100");
		_fractionToYield = 1 - (percentage / 100);
		
		_t0 = now();
	}


	void yield() {
		long sleepStart = now();

		adaptTimeToWait(sleepStart);
		sleep(sleepStart);
	}


	private void adaptTimeToWait(long now) {
		long ellapsed = now - _t0;
		
		if (_waited / ellapsed < _fractionToYield)
			_millisToWait *= 1.5;
		else 
			_millisToWait /= 1.5;
	}
	
	
	private void sleep(long sleepStart) {
		if (_millisToWait < 1) return;

		my(Threads.class).sleepWithoutInterruptions((long)_millisToWait);
		_waited = now() - sleepStart;
	}


	private long now() {
		return System.currentTimeMillis();
	}
	
}


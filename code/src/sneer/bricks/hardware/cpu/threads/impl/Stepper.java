package sneer.bricks.hardware.cpu.threads.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.cpu.threads.throttle.CpuThrottle;

class Stepper implements Runnable, Contract {

	private final Runnable _steppable;
	private final float _waitFactor = waitFactor();
	private volatile boolean _isDisposed = false;

	
	Stepper(Runnable steppable) {
		_steppable = steppable;
	}

	
	@Override
	public void run() {
		if (_waitFactor != 0)
			runWithCpuThrottle();
		else
			while (!_isDisposed) _steppable.run();
	}


	private void runWithCpuThrottle() {
		while (!_isDisposed) {
			long cpuTimeNanos = stepAndMeasureCpuTime();
			
			long waitTimeMillis = (long)(cpuTimeNanos * _waitFactor / 1000000);
			my(Threads.class).sleepWithoutInterruptions(waitTimeMillis);
		}
	}


	private long stepAndMeasureCpuTime() {
		long t0 = System.nanoTime();
		_steppable.run();
		return System.nanoTime() - t0;
	}

	
	@Override
	public void dispose() {
		_isDisposed = true;
	}

	
	private float waitFactor() {
		int runFactor = my(CpuThrottle.class).maxCpuUsage();
		return ((float)(100 - runFactor)) / runFactor;
	}
}


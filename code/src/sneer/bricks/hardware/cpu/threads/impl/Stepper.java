package sneer.bricks.hardware.cpu.threads.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.threads.throttle.CpuThrottle;

class Stepper implements Runnable, Contract {

	private final Runnable _steppable;
	private volatile boolean _isDisposed = false;

	
	Stepper(Runnable steppable) {
		_steppable = steppable;
	}

	
	@Override
	public void run() {
		while (!_isDisposed) {
			_steppable.run();
			my(CpuThrottle.class).yield();
		}
	}


	@Override
	public void dispose() {
		_isDisposed = true;
	}
	
	
}


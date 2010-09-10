package sneer.bricks.hardware.cpu.threads.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.threads.throttle.CpuThrottle;
import sneer.foundation.lang.Closure;

class Stepper implements Closure, Contract {

	private final Closure _steppable;
	private volatile boolean _isDisposed = false;

	
	Stepper(Closure steppable) {
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


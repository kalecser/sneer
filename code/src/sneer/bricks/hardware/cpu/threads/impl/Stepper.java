package sneer.bricks.hardware.cpu.threads.impl;

import static basis.environments.Environments.my;
import basis.lang.Closure;
import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.threads.throttle.CpuThrottle;

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


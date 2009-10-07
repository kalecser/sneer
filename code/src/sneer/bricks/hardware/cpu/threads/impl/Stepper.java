package sneer.bricks.hardware.cpu.threads.impl;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;

class Stepper implements Runnable, Contract {

	private final Runnable _steppable;
	private volatile boolean _isDisposed = false;

	Stepper(Runnable steppable) {
		_steppable = steppable;
	}

	@Override
	public void run() {
		while (!_isDisposed) _steppable.run();
	}

	@Override
	public void dispose() {
		_isDisposed = true;
	}
}

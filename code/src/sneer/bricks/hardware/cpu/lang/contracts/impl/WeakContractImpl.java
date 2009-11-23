package sneer.bricks.hardware.cpu.lang.contracts.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.Disposable;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;

class WeakContractImpl implements WeakContract {

	private Disposable _service;
	private final Environment _environment;

	WeakContractImpl(Disposable service) {
		_service = service;
		_environment = my(Environment.class);
	}

	@Override
	synchronized
	public void dispose() {
		if (_service == null) return;
		_service.dispose();
		_service = null;
	}

	@Override
	protected void finalize() throws Throwable {
		if (_service != null)
			Environments.runWith(_environment, new Runnable() { @Override public void run() {
				my(Logger.class).log("Weak Contract gc'd: " + _service);
			}});
		
		dispose();
	}

}

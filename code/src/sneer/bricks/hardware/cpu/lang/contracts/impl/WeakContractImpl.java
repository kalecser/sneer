package sneer.bricks.hardware.cpu.lang.contracts.impl;

import static basis.environments.Environments.my;
import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.Closure;
import sneer.bricks.hardware.cpu.lang.contracts.Disposable;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;

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
			Environments.runWith(_environment, new Closure() { @Override public void run() {
				my(Logger.class).log("Weak Contract gc'd: " + _service);
			}});
		
		dispose();
	}

}

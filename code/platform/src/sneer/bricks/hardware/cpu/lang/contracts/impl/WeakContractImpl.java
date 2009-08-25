package sneer.bricks.hardware.cpu.lang.contracts.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.Disposable;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;

class WeakContractImpl implements WeakContract {

	@SuppressWarnings("unused")	private final Object _annexToAvoidGc;
	private Disposable _service;

	WeakContractImpl(Disposable service, Object annexToAvoidGc) {
		_annexToAvoidGc = annexToAvoidGc;
		_service = service;
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
			my(Logger.class).log("Weak Contract gc'd: " + _service);
		dispose();
	}

}

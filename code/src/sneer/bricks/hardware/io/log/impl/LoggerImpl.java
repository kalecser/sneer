package sneer.bricks.hardware.io.log.impl;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.io.log.worker.LogWorker;
import sneer.bricks.hardware.io.log.worker.LogWorkerHolder;
import static basis.environments.Environments.my;

class LoggerImpl implements Logger {

	private LogWorker _delegate;

	
	@Override
	public void log(String message, Object... messageInsets) {
		if (_delegate == null) {
			_delegate = my(LogWorkerHolder.class).worker();
			if (_delegate == null) return;
		}
		
		_delegate.log(message, messageInsets);
	}

}
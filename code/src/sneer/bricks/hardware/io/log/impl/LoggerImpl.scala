package sneer.bricks.hardware.io.log.impl

import sneer.bricks.hardware.io.log.Logger
import sneer.bricks.hardware.io.log.worker.LogWorker
import sneer.bricks.hardware.io.log.worker.LogWorkerHolder
import sneer.foundation.environments.Environments.my

class ScalaLoggerImpl extends Logger {

	var _delegate: LogWorker = null;
 
	override def log(message: String, messageInsets: AnyRef*): Unit = {
		if (_delegate == null) {
			_delegate = my(classOf[LogWorkerHolder]).worker();
			if (_delegate == null) return;
		}
		
		_delegate.log(message, messageInsets);
	}

}
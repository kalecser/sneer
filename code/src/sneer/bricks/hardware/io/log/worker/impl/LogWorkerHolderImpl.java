package sneer.bricks.hardware.io.log.worker.impl;

import sneer.bricks.hardware.io.log.worker.LogWorker;
import sneer.bricks.hardware.io.log.worker.LogWorkerHolder;

class LogWorkerHolderImpl implements LogWorkerHolder {

	private LogWorker _worker;

	@Override
	public void setWorker(LogWorker worker) {
		if (_worker != null) throw new IllegalStateException("Log Worker was already set.");
		_worker = worker;
	}

	@Override
	public LogWorker worker() {
		return _worker;
	}

}

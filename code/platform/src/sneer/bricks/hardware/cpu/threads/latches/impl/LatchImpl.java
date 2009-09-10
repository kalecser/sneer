package sneer.bricks.hardware.cpu.threads.latches.impl;

import java.util.concurrent.CountDownLatch;

import sneer.bricks.hardware.cpu.threads.latches.Latch;

class LatchImpl implements Latch {

	CountDownLatch _delegate = new CountDownLatch(1);
	
	@Override
	public void waitTillOpen() {
		try {
			_delegate.await();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void run() {
		open();
	}

	@Override
	public void open() {
		_delegate.countDown();
	}

}

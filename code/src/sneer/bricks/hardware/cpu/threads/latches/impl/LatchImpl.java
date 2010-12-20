package sneer.bricks.hardware.cpu.threads.latches.impl;

import java.util.concurrent.CountDownLatch;

import sneer.bricks.hardware.cpu.threads.latches.Latch;

class LatchImpl implements Latch {

	private final CountDownLatch _delegate;

	LatchImpl(int count) {
		_delegate = new CountDownLatch(count);
	}

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
		countDown();
	}

	@Override
	public void open() {
		countDown();
	}

	@Override
	public void countDown() {
		_delegate.countDown();
	}

	@Override
	public boolean isOpen() {
		return _delegate.getCount() == 0;
	}
	
}

package basis.util.concurrent;

import java.util.concurrent.CountDownLatch;

import basis.lang.Consumer;


public class RefLatch<T> implements Consumer<T>{

	private final CountDownLatch delegate = new CountDownLatch(1);
	private volatile T value;

	
	public T waitAndGet() {
		try {
			delegate.await();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
		return value;
	}

	
	@Override
	public void consume(T value) {
		delegate.countDown();
		this.value = value;
	}

}

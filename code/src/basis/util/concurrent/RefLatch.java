package basis.util.concurrent;

import java.util.concurrent.CountDownLatch;


public class RefLatch<T> {

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

	
	public void set(T value) {
		delegate.countDown();
		this.value = value;
	}

}

package sneer.bricks.hardware.cpu.threads.latches;


/** Same as a java.util.concurrent.CountDownLatch.class, but it does not throw InterruptedException. Throws IllegalState instead.
 * @see java.util.concurrent.CountDownLatch.class */
public interface Latch extends Runnable {

	/** Decrement the count of this latch. */
	void countDown();

	/** See waitTillOpen() */
	void open(); // the same as countDown(), but used if CountDownLatches of 1
	boolean isOpen();
	
	/** Waits for some other thread to open() this latch. If this latch has already been opened, returns immediately. */
	void waitTillOpen();

	/** Decrement the count of this latch. */
	@Override
	void run();

}

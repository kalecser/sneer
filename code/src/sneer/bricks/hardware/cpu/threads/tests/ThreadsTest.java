package sneer.bricks.hardware.cpu.threads.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import sneer.foundation.util.concurrent.Latch;

public class ThreadsTest extends BrickTestWithThreads {

	private final Threads _subject = my(Threads.class);

	@Test (timeout = 2000)
	public void environmentIsPropagatedToSteppables() throws Exception {
		final Environment environment = my(Environment.class);
		final Latch latch = new Latch();

		_subject.startStepping(new Closure() { @Override public void run() {
			assertSame(environment, Environments.my(Environment.class));
			latch.open();
		}});
		
		latch.waitTillOpen();
	}

	@Test (timeout = 2000)
	public void threadsAreCrashed() {
		crashAllThreads();

		_subject.waitUntilCrash();
	}

	private void crashAllThreads() {
		final Environment environment = my(Environment.class);
		
		Thread thread = new Thread() { @Override public void run(){
			Environments.runWith(environment, new Closure()  { @Override public void run() {
				_subject.crashAllThreads();
			}});
		}};
		thread.start();
	}
	
	@Test (timeout = 2000)
	public void crashHandlersAreNotified() {
		
		final Latch crashingLatch = new Latch();
		@SuppressWarnings("unused")
		WeakContract crashingContract = my(Threads.class).crashed().addPulseReceiver(crashingLatch);
		
		crashAllThreads();

		_subject.waitUntilCrash();
		
		crashingLatch.waitTillOpen();
	}

}

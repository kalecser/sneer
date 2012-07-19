package sneer.bricks.pulp.notifiers.tests;

import static basis.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.notifiers.Notifier;
import sneer.bricks.pulp.notifiers.Notifiers;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.lang.ByRef;
import basis.lang.Closure;
import basis.lang.Producer;
import basis.util.concurrent.Latch;

public class EventNotifiersTest extends BrickTestBase {
	
	@Test (expected = Throwable.class)
	public void throwablesBubbleUpDuringTests() {
		Producer<Object> welcomeEventProducer = new Producer<Object>() { @Override public Object produce() {
			throw new Error();
		}};
		Notifier<Object> notifier = my(Notifiers.class).newInstance(welcomeEventProducer);
		notifier.output().addReceiver(my(Signals.class).sink());
	}
	
	
	@Test (timeout = 2000)
	public void actsAsPulser() {
		final Notifier<Object> notifier = my(Notifiers.class).newInstance();
		final Latch pulseLatch = new Latch();
		@SuppressWarnings("unused")
		final WeakContract pulseContract = notifier.output().addPulseReceiver(pulseLatch);
		
		notifier.notifyReceivers("foo");
		
		pulseLatch.waitTillOpen();
	}

	
	@Test (timeout = 6000)
	public void contractWeakness() throws Exception {
		final ByRef<Boolean> finalized = ByRef.newInstance(false);
		
		@SuppressWarnings("unused")
		Object ref = my(Notifiers.class).newInstance().output().addPulseReceiver(new Closure() {
			@Override
			public void run() {
				return;
			}

			@Override
			protected void finalize() throws Throwable {
				finalized.value = true; 
			}
		});
		
		ref = null;

		while (!finalized.value) {
			System.gc();
			Thread.sleep(20);
		}
	}

}

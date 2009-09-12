package sneer.bricks.pulp.events.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.pulp.events.EventNotifier;
import sneer.bricks.pulp.events.EventNotifiers;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.lang.Producer;

public class EventNotifiersTest extends BrickTest {
	
	@Test (expected = Throwable.class)
	public void throwablesBubbleUpDuringTests() {
		Producer<Object> welcomeEventProducer = new Producer<Object>() { @Override public Object produce() {
			throw new Error();
		}};
		EventNotifier<Object> notifier = my(EventNotifiers.class).newInstance(welcomeEventProducer);
		notifier.output().addReceiver(my(Signals.class).sink());
	}
	
	@Test (timeout = 2000)
	public void actsAsPulser() {
		final EventNotifier<Object> notifier = my(EventNotifiers.class).newInstance();
		final Latch pulseLatch = my(Latches.class).produce();
		@SuppressWarnings("unused")
		final WeakContract pulseContract = notifier.output().addPulseReceiver(pulseLatch);
		
		notifier.notifyReceivers("foo");
		
		pulseLatch.waitTillOpen();
	}

}

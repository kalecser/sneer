package wheel.reactive.impl.tests;

import org.junit.Test;

import sneer.pulp.config.persistence.testsupport.BrickTest;
import wheel.lang.Consumer;
import wheel.reactive.Signals;
import wheel.reactive.impl.AbstractNotifier;

public class AbstractNotifierTest extends BrickTest {
	
	@Test (expected = Throwable.class)
	public void throwablesBubbleUpDuringTests() {
		new AbstractNotifier<Object>() {
			@Override
			protected void initReceiver(Consumer<Object> receiver) {
				throw new Error();
			}
		}.addReceiver(Signals.sink());
	}

}

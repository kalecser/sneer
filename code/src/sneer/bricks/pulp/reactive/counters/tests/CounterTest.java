package sneer.bricks.pulp.reactive.counters.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.reactive.counters.Counter;
import sneer.bricks.pulp.reactive.counters.Counters;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class CounterTest extends BrickTest {

	@Test
	public void countTest() {
		Counter counter = my(Counters.class).newInstance(0);

		Runnable incrementer = counter.incrementer();
		Runnable boundlessDecrementer = counter.decrementer();
		Runnable nonNegativeDecrementer = wrapDecrementerToAvoidNegativeValues(counter);

		assertCurrentCountIs(0, counter);

		incrementer.run();
		assertCurrentCountIs(1, counter);

		incrementer.run();
		assertCurrentCountIs(2, counter);

		incrementer.run();
		assertCurrentCountIs(3, counter);


		boundlessDecrementer.run();
		assertCurrentCountIs(2, counter);

		boundlessDecrementer.run();
		assertCurrentCountIs(1, counter);

		boundlessDecrementer.run();
		assertCurrentCountIs(0, counter);

		try {
			nonNegativeDecrementer.run();
			fail();
		} catch (IllegalStateException ignore) {}

		boundlessDecrementer.run();
		assertCurrentCountIs(-1, counter);
	}

	private Runnable wrapDecrementerToAvoidNegativeValues(final Counter counter) {
		return new Runnable() {
			private final Runnable _delegate = counter.decrementer(); 

			@Override public void run() {
				if (counter.count().currentValue() == 0)
					throw new IllegalStateException("Counter cannot hold a negative value");
				_delegate.run();
			}
		};
	}

	private void assertCurrentCountIs(int expected, Counter counter) {
		my(SignalUtils.class).waitForValue(counter.count(), expected);
	}
                         
}

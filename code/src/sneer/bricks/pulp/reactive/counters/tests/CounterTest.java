package sneer.bricks.pulp.reactive.counters.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.reactive.counters.Counter;
import sneer.bricks.pulp.reactive.counters.Counters;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class CounterTest extends BrickTestBase {

	@Test
	public void countTest() {
		Counter counter = my(Counters.class).newInstance(0);

		Runnable incrementer = counter.incrementer();
		Runnable boundlessDecrementer = counter.decrementer();
		Runnable nonNegativeDecrementer = counter.conditionalDecrementer(counter.count().currentValue() > 0);

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

		nonNegativeDecrementer.run();
		assertCurrentCountIs(0, counter);		

		boundlessDecrementer.run();
		assertCurrentCountIs(-1, counter);
	}

	private void assertCurrentCountIs(int expected, Counter counter) {
		my(SignalUtils.class).waitForValue(counter.count(), expected);
	}

}

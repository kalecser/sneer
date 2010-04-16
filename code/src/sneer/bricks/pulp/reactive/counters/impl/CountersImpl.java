package sneer.bricks.pulp.reactive.counters.impl;

import sneer.bricks.pulp.reactive.counters.Counter;
import sneer.bricks.pulp.reactive.counters.Counters;

class CountersImpl implements Counters {

	@Override
	public Counter newInstance(int initialValue) {
		return new CounterImpl(initialValue);
	}

}

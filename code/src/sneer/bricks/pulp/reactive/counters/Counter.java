package sneer.bricks.pulp.reactive.counters;

import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.lang.Closure;

public interface Counter {

	Signal<Integer> count();

	Closure incrementer();

	Closure decrementer();

}

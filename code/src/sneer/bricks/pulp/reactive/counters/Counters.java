package sneer.bricks.pulp.reactive.counters;

import sneer.foundation.brickness.Brick;

@Brick
public interface Counters {

	Counter newInstance(int initialValue);

}

package sneer.bricks.expression.tuples.floodcache;

import sneer.bricks.expression.tuples.Tuple;
import sneer.foundation.brickness.Brick;

@Brick
public interface FloodedTupleCache {

	int size();

	boolean add(Tuple tuple);

}

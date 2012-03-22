package sneer.bricks.expression.tuples.floodcache;

import basis.brickness.Brick;
import sneer.bricks.expression.tuples.Tuple;

@Brick
public interface FloodedTupleCache {

	int maxSize();

	boolean add(Tuple tuple);

}

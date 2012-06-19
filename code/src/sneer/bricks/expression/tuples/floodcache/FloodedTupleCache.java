package sneer.bricks.expression.tuples.floodcache;

import sneer.bricks.expression.tuples.Tuple;
import basis.brickness.Brick;

@Brick
public interface FloodedTupleCache {

	int maxSize();

	boolean add(Tuple tuple);

	boolean contains(Tuple tuple);

}

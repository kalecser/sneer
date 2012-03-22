package sneer.bricks.pulp.datastructures.cache;

import basis.brickness.Brick;

@Brick
public interface CacheFactory {

	<T> Cache<T> createWithCapacity(int capacity);

}

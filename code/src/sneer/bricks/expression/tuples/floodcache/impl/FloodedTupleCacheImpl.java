package sneer.bricks.expression.tuples.floodcache.impl;

import java.util.Iterator;
import java.util.LinkedHashSet;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.floodcache.FloodedTupleCache;

class FloodedTupleCacheImpl implements FloodedTupleCache {

	private static final int FLOODED_CACHE_SIZE = 1000;
	
	private final LinkedHashSet<Tuple> _cache = new LinkedHashSet<Tuple>();

	
	@Override
	public int size() {
		return FLOODED_CACHE_SIZE;
	}

	
	@Override
	public boolean add(Tuple tuple) {
		try {
			return _cache.add(tuple);
		} finally {
			capSize();
		}
	}

	
	private void capSize() {
		if (_cache.size() <= FLOODED_CACHE_SIZE) return;
		
		Iterator<Tuple> tuplesIterator = _cache.iterator();
		tuplesIterator.next();
		tuplesIterator.remove();
	}
	
}

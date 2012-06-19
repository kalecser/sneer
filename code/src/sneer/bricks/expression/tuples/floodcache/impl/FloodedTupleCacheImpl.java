package sneer.bricks.expression.tuples.floodcache.impl;

import java.util.Iterator;
import java.util.LinkedHashSet;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.floodcache.FloodedTupleCache;

class FloodedTupleCacheImpl implements FloodedTupleCache {

	private static final int MAX_CACHE_SIZE = 1000;
	
	private final LinkedHashSet<Tuple> _cache = new LinkedHashSet<Tuple>();

	
	@Override
	public int maxSize() {
		return MAX_CACHE_SIZE;
	}

	
	@Override
	public boolean add(Tuple tuple) {
		try {
			return _cache.add(tuple);
		} finally {
			capSize();
		}
	}
	
	
	@Override
	public boolean contains(Tuple tuple) {
		return _cache.contains(tuple);
	}

	
	private void capSize() {
		if (_cache.size() <= MAX_CACHE_SIZE) return;
		
		Iterator<Tuple> tuplesIterator = _cache.iterator();
		tuplesIterator.next();
		tuplesIterator.remove();
	}
	
}

package sneer.bricks.expression.tuples.keeper.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.List;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.expression.tuples.keeper.TupleKeeper;
import sneer.bricks.expression.tuples.kept.KeptTuples;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Functor;
import sneer.foundation.lang.Predicate;
import sneer.foundation.lang.Producer;


class TupleKeeperImpl implements TupleKeeper {

	private static final KeptTuples KeptTuples = my(KeptTuples.class);

	private static final Producer<List<Predicate<?>>> FILTER_LIST_PRODUCER = new Producer<List<Predicate<?>>>() {  @Override public List<Predicate<?>> produce() {
		return new ArrayList<Predicate<?>>();
	}};
	
	
	private final CacheMap<Class<? extends Tuple>, List<Predicate<?>>> _filtersByType = CacheMap.newInstance();

	
	@Override
	public void keepType(Class<? extends Tuple> tupleType) {
		keepChosen(tupleType, Predicate.TRUE);
	}

	
	@Override
	public <T extends Tuple> void keepNewest(Class<T> tupleType, Functor<? super T, Object> grouping) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	
	@Override
	public <T extends Tuple> void keepChosen(Class<T> tupleType, Predicate<? super T> filter) {
		List<Predicate<?>> filters = _filtersByType.get(tupleType, FILTER_LIST_PRODUCER);
		filters.add(filter);
	}

	
	@Override
	public Tuple[] keptTuples() {
		return KeptTuples.all();
	}

	
	@Override
	public boolean isAlreadyKept(Tuple tuple) {
		return KeptTuples.contains(tuple);
	}

	
	@Override
	public void keepIfNecessary(Tuple tuple) {
		if (shouldKeep(tuple)) KeptTuples.add(tuple);
	}
	
	
	@Override
	public void garbageCollect() {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	
	private boolean shouldKeep(Tuple tuple) {
		List<Predicate<?>> filters = _filtersByType.get(tuple.getClass());
		if (filters == null) return false;
		
		for (Predicate<?> filter : filters)
			if (((Predicate<? super Tuple>)filter).evaluate(tuple)) return true;

		return false;
	}

}

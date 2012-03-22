package sneer.bricks.expression.tuples.keeper.impl;

import basis.lang.CacheMap;
import basis.lang.Functor;
import basis.lang.Predicate;
import sneer.bricks.expression.tuples.Tuple;

final class NewestTupleByGroup<T extends Tuple> implements Predicate<T> {
	
	private final Functor<? super T, Object> _grouping;
	private final CacheMap<Object, Long> _newestPublicationTimeByGroup = CacheMap.newInstance();

	
	public NewestTupleByGroup(Functor<? super T, Object> grouping) {
		_grouping = grouping;
	}

	
	@Override public boolean evaluate(T tuple) {
		Object group = _grouping.evaluate(tuple);
		Long newestPublication = _newestPublicationTimeByGroup.get(group, 0L);
		if (tuple.publicationTime >= newestPublication) {
			_newestPublicationTimeByGroup.put(group, tuple.publicationTime);
			return true;
		}
		return false;
	}

}
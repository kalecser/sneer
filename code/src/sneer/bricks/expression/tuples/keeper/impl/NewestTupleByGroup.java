package sneer.bricks.expression.tuples.keeper.impl;

import sneer.bricks.expression.tuples.Tuple;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Functor;
import sneer.foundation.lang.Predicate;

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
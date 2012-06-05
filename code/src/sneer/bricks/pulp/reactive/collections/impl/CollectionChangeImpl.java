package sneer.bricks.pulp.reactive.collections.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import sneer.bricks.pulp.reactive.collections.CollectionChange;


class CollectionChangeImpl<T> implements CollectionChange<T> {

	private final Collection<T> _elementsAdded;
	private final Collection<T> _elementsRemoved;

	CollectionChangeImpl(T elementAdded, T elementRemoved) {
		this(wrap(elementAdded), wrap(elementRemoved)); //Optimize
	}

	CollectionChangeImpl(Collection<? extends T> added, Collection<T> removed) {
		_elementsAdded = nullToEmpty(added);
		_elementsRemoved = nullToEmpty(removed);
	}

	private static <CT> Collection<CT> wrap(CT element) {
		if (element == null) return null;
		Collection<CT> result = new ArrayList<CT>(1);
		result.add(element);
		return result;
	}

	private static <CT> Collection<CT> nullToEmpty(Collection<? extends CT> collection) {
		return collection == null ? Collections.EMPTY_LIST : collection;
	}

	@Override
	public Collection<T> elementsAdded() {
		return _elementsAdded;
	}

	@Override
	public Collection<T> elementsRemoved() {
		return _elementsRemoved;
	}

}
package sneer.bricks.pulp.reactive.collections.impl;

import java.util.Arrays;
import java.util.Collection;

final class ListElementReplaced<T> extends AbstractListValueChange<T> {

	protected final T _newElement;

	ListElementReplaced(int index, T oldElement, T newElement) {
		super(index, oldElement);
		_newElement = newElement;
	}

	@Override
	public void accept(Visitor<T> visitor) {
		visitor.elementReplaced(_index, _element, _newElement);
	}

	@Override
	public Collection<T> elementsAdded() {
		return Arrays.asList(_newElement);
	}

	@Override
	public Collection<T> elementsRemoved() {
		return Arrays.asList(_element);
	}
}
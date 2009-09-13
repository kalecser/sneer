/**
 * 
 */
package sneer.bricks.hardware.ram.arrays.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import sneer.bricks.hardware.ram.arrays.ImmutableArray;

final class ImmutableArrayImpl<T> implements ImmutableArray<T> {
	
	private final T[] _elements;

	ImmutableArrayImpl(Collection<T> elements) {
		_elements = (T[]) elements.toArray(new Object[elements.size()]);
	}

	public ImmutableArrayImpl(T[] elements) {
		_elements = elements.clone();
	}

	@Override
	public Iterator<T> iterator() {
		return Collections.unmodifiableCollection(Arrays.asList(_elements)).iterator();
	}

	@Override
	public int length() {
		return _elements.length;
	}

	@Override
	public T[] toArray() {
		return _elements.clone();
	}
}
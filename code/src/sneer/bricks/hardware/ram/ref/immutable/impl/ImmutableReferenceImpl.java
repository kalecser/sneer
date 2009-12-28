package sneer.bricks.hardware.ram.ref.immutable.impl;

import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;

class ImmutableReferenceImpl<T> implements ImmutableReference<T> {

	private T _value;
	private boolean _isAlreadySet; //This is necessary because _value can be set to null;
	
	ImmutableReferenceImpl() {}
	ImmutableReferenceImpl(T value) { set(value); }

	@Override
	public T get() {
		if (!_isAlreadySet) throw new IllegalStateException("Value was not yet set for this Immutable.");
		return _value;
	}

	@Override
	public void set(T value) {
		if (_isAlreadySet) throw new IllegalStateException("Value was already set for this Immutable.");
		_isAlreadySet = true;
		_value = value;
	}

}

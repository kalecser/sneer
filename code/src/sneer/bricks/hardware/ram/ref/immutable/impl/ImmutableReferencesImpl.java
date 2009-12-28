package sneer.bricks.hardware.ram.ref.immutable.impl;

import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReferences;

class ImmutableReferencesImpl implements ImmutableReferences {

	@Override
	public <T> ImmutableReference<T> newInstance() {
		return new ImmutableReferenceImpl<T>();
	}

	@Override
	public <T> ImmutableReference<T> newInstance(T value) {
		return new ImmutableReferenceImpl<T>(value);
	}

}

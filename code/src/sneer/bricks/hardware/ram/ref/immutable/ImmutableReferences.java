package sneer.bricks.hardware.ram.ref.immutable;

import basis.brickness.Brick;

@Brick
public interface ImmutableReferences {

	/** @return an immutable that has not been set yet. It must be set before getting its value.*/
	<T> ImmutableReference<T> newInstance();

	/** @return an immutable set to value.*/
	<T> ImmutableReference<T> newInstance(T value);

}

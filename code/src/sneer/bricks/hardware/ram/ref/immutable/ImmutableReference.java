package sneer.bricks.hardware.ram.ref.immutable;

public interface ImmutableReference<T> {

	void set(T value);
	T get();
	
}

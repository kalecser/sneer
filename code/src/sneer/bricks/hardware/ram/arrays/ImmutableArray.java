package sneer.bricks.hardware.ram.arrays;

import java.util.Collection;

public interface ImmutableArray<T> extends Collection<T> {

	abstract int length();

}

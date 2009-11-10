package sneer.foundation.brickness.impl.tests.fixtures.runtimenature.brick;

import sneer.foundation.brickness.Brick;
import sneer.foundation.brickness.impl.tests.fixtures.runtimenature.nature.SomeRuntimeNature;

@Brick(SomeRuntimeNature.class)
public interface BrickOfSomeRuntimeNature {

	void foo();

	String bar();
	
	int baz();

	void foo(String arg);

	int add(int i, int j);
	
	Greeter newGreeter();

}

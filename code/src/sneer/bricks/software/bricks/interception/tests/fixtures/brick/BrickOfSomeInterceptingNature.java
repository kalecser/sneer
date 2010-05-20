package sneer.bricks.software.bricks.interception.tests.fixtures.brick;

import sneer.bricks.software.bricks.interception.tests.fixtures.nature.SomeInterceptingNature;
import sneer.foundation.brickness.Brick;

@Brick(SomeInterceptingNature.class)
public interface BrickOfSomeInterceptingNature {

	int baz();

	int add(int i, int j);
	
	Greeter newGreeter();

	void intraBrickMethod();

}

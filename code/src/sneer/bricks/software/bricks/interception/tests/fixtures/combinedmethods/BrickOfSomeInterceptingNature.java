package sneer.bricks.software.bricks.interception.tests.fixtures.combinedmethods;

import sneer.bricks.software.bricks.interception.tests.fixtures.nature.SomeInterceptingNature;
import sneer.foundation.brickness.Brick;

@Brick(SomeInterceptingNature.class)
public interface BrickOfSomeInterceptingNature {

	int add(int i, int j);
	
	Greeter newGreeter();

	void intraBrickMethod();

}

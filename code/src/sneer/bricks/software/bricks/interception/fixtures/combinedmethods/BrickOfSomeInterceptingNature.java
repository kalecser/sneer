package sneer.bricks.software.bricks.interception.fixtures.combinedmethods;

import basis.brickness.Brick;
import sneer.bricks.software.bricks.interception.fixtures.nature.SomeInterceptingNature;

@Brick(SomeInterceptingNature.class)
public interface BrickOfSomeInterceptingNature {

	int add(int i, int j);
	
	Greeter newGreeter();

	void intraBrickMethod();

}

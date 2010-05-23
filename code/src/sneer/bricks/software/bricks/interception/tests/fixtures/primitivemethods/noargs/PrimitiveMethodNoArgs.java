package sneer.bricks.software.bricks.interception.tests.fixtures.primitivemethods.noargs;

import sneer.bricks.software.bricks.interception.tests.fixtures.nature.SomeInterceptingNature;
import sneer.foundation.brickness.Brick;

@Brick(SomeInterceptingNature.class)
public interface PrimitiveMethodNoArgs {
	
	int baz();

}

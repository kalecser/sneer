package sneer.bricks.software.bricks.interception.fixtures.primitivemethods.noargs;

import basis.brickness.Brick;
import sneer.bricks.software.bricks.interception.fixtures.nature.SomeInterceptingNature;

@Brick(SomeInterceptingNature.class)
public interface PrimitiveMethodNoArgs {
	
	int baz();

}

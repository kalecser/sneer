package sneer.bricks.software.bricks.interception.fixtures.voidmethods.noargs;

import sneer.bricks.software.bricks.interception.fixtures.nature.SomeInterceptingNature;
import sneer.foundation.brickness.Brick;

@Brick(SomeInterceptingNature.class)
public interface VoidMethodsNoArgs {

	void foo();

}

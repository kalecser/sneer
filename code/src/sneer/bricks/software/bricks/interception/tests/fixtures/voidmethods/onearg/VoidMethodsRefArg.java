package sneer.bricks.software.bricks.interception.tests.fixtures.voidmethods.onearg;

import sneer.bricks.software.bricks.interception.tests.fixtures.nature.SomeInterceptingNature;
import sneer.foundation.brickness.Brick;

@Brick(SomeInterceptingNature.class)
public interface VoidMethodsRefArg {

	void foo(String arg);

}

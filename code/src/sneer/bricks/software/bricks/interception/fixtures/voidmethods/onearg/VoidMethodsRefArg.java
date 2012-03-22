package sneer.bricks.software.bricks.interception.fixtures.voidmethods.onearg;

import basis.brickness.Brick;
import sneer.bricks.software.bricks.interception.fixtures.nature.SomeInterceptingNature;

@Brick(SomeInterceptingNature.class)
public interface VoidMethodsRefArg {

	void foo(String arg);

}

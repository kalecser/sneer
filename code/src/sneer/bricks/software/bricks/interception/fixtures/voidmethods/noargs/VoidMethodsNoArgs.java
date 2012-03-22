package sneer.bricks.software.bricks.interception.fixtures.voidmethods.noargs;

import basis.brickness.Brick;
import sneer.bricks.software.bricks.interception.fixtures.nature.SomeInterceptingNature;

@Brick(SomeInterceptingNature.class)
public interface VoidMethodsNoArgs {

	void foo();

}

package sneer.bricks.software.bricks.interception.fixtures.refmethods.noargs;

import sneer.bricks.software.bricks.interception.fixtures.nature.SomeInterceptingNature;
import sneer.foundation.brickness.Brick;

@Brick(SomeInterceptingNature.class)
public interface RefMethodsNoArgs {

	String bar();
	
}

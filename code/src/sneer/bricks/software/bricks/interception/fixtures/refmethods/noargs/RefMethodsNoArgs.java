package sneer.bricks.software.bricks.interception.fixtures.refmethods.noargs;

import basis.brickness.Brick;
import sneer.bricks.software.bricks.interception.fixtures.nature.SomeInterceptingNature;

@Brick(SomeInterceptingNature.class)
public interface RefMethodsNoArgs {

	String bar();
	
}

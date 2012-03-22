package sneer.bricks.software.bricks.interception.fixtures.brickwithlib;

import basis.brickness.Brick;
import sneer.bricks.software.bricks.interception.fixtures.nature.SomeInterceptingNature;

@Brick(SomeInterceptingNature.class)
public interface BrickWithLib {

	int fooBar();

}

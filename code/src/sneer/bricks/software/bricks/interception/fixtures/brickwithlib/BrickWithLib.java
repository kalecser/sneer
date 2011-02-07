package sneer.bricks.software.bricks.interception.fixtures.brickwithlib;

import sneer.bricks.software.bricks.interception.fixtures.nature.SomeInterceptingNature;
import sneer.foundation.brickness.Brick;

@Brick(SomeInterceptingNature.class)
public interface BrickWithLib {

	int fooBar();

}

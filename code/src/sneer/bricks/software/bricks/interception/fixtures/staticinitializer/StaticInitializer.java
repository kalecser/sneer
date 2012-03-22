package sneer.bricks.software.bricks.interception.fixtures.staticinitializer;

import basis.brickness.Brick;
import sneer.bricks.software.bricks.interception.fixtures.nature.SomeInterceptingNature;

@Brick(SomeInterceptingNature.class)
public interface StaticInitializer {

	void foo();

}

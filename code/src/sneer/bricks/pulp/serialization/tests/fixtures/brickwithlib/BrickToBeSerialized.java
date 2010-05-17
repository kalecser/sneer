package sneer.bricks.pulp.serialization.tests.fixtures.brickwithlib;

import sneer.foundation.brickness.Brick;

@Brick
public interface BrickToBeSerialized {

	ClassLoader libClassLoader();

}

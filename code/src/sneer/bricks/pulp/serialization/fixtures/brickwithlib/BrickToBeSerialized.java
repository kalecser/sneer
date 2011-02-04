package sneer.bricks.pulp.serialization.fixtures.brickwithlib;

import sneer.foundation.brickness.Brick;

@Brick
public interface BrickToBeSerialized {

	ClassLoader libClassLoader();

}

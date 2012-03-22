package sneer.bricks.pulp.serialization.fixtures.brickwithlib;

import basis.brickness.Brick;

@Brick
public interface BrickToBeSerialized {

	ClassLoader libClassLoader();

}

package sneer.bricks.pulp.serialization.fixtures.brickwithlib.impl;

import sneer.bricks.pulp.serialization.fixtures.brickwithlib.BrickToBeSerialized;
import foo.LibForSerializer;

class BrickToBeSerializedImpl implements BrickToBeSerialized {

	@Override
	public ClassLoader libClassLoader() {
		return LibForSerializer.class.getClassLoader();
	}
}

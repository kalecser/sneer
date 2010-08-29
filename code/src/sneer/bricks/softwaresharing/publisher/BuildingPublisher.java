package sneer.bricks.softwaresharing.publisher;

import java.io.IOException;

import sneer.foundation.brickness.Brick;

@Brick
public interface BuildingPublisher {

	BuildingHash publishMyOwnBuilding() throws IOException;

}

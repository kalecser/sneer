package sneer.bricks.softwaresharing.publisher;

import java.io.IOException;

import basis.brickness.Brick;


@Brick
public interface BuildingPublisher {

	BuildingHash publishMyOwnBuilding() throws IOException;

}

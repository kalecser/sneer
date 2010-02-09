package sneer.bricks.softwaresharing;

import java.util.Collection;

import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.foundation.brickness.Brick;

@Brick
public interface BrickSpace {

	EventSource<Seal> newBuildingFound();

	Collection<BrickInfo> availableBricks();
	
}

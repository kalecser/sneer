package sneer.bricks.softwaresharing;

import java.util.Collection;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.events.EventSource;
import sneer.foundation.brickness.Brick;

@Brick
public interface BrickSpace {

	EventSource<Seal> newBuildingFound();

	Collection<BrickHistory> availableBricks();
	
}

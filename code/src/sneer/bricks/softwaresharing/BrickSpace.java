package sneer.bricks.softwaresharing;

import java.util.Collection;

import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.software.bricks.snappstarter.Snapp;
import sneer.foundation.brickness.Brick;
import sneer.foundation.brickness.Seal;

@Snapp
@Brick
public interface BrickSpace {

	EventSource<Seal> newBuildingFound();

	Collection<BrickInfo> availableBricks();
	
}

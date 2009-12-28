package sneer.bricks.softwaresharing;

import java.util.Collection;

import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.bricks.software.bricks.snappstarter.Snapp;
import sneer.foundation.brickness.Brick;

@Snapp
@Brick
public interface BrickSpace {

	EventSource<Seal> newBuildingFound();

	Collection<BrickInfo> availableBricks();
	
}

package sneer.bricks.softwaresharing;

import java.util.Collection;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.notifiers.Source;
import sneer.foundation.brickness.Brick;

@Brick
public interface BrickSpace {

	Source<Seal> newBuildingFound();

	Collection<BrickHistory> availableBricks();
	
}

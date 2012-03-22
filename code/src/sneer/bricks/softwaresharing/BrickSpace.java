package sneer.bricks.softwaresharing;

import java.util.Collection;

import basis.brickness.Brick;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.notifiers.Source;

@Brick
public interface BrickSpace {

	Source<Seal> newBuildingFound();

	Collection<BrickHistory> availableBricks();
	
}

package sneer.snapps.games.drones.units;

import sneer.snapps.games.drones.units.Unit.Direction;
import basis.brickness.Brick;

@Brick
public interface Units {

	Unit create(int x, Direction direction);

}

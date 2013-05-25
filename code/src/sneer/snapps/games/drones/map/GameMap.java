package sneer.snapps.games.drones.map;

import sneer.snapps.games.drones.units.Unit;
import basis.brickness.Brick;

@Brick
public interface GameMap {

	void step();

	Unit unit1();

	Unit unit2();
	

}

package sneer.snapps.games.drones.map.impl;

import static basis.environments.Environments.my;
import sneer.snapps.games.drones.map.GameMap;
import sneer.snapps.games.drones.units.Unit;
import sneer.snapps.games.drones.units.Units;

class GameMapImpl implements GameMap {

	private final Unit unit1 = my(Units.class).create(  0, Unit.Direction.RIGHT);
	private final Unit unit2 = my(Units.class).create(700, Unit.Direction.LEFT);
	
	@Override
	public void step() {
		if (!unit1.collidesWith(unit2)) {
			unit1.move();
			unit2.move();
		}
		
	}

	@Override
	public Unit unit1() {
		return unit1;
	}

	@Override
	public Unit unit2() {
		return unit2;
	}

}

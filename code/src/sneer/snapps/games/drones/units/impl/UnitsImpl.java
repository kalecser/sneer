package sneer.snapps.games.drones.units.impl;

import sneer.snapps.games.drones.units.Unit;
import sneer.snapps.games.drones.units.Unit.Direction;
import sneer.snapps.games.drones.units.Units;

class UnitsImpl implements Units {

	@Override
	public Unit create(int x, Direction direction, String name) {
		return new UnitImpl(x, direction, name);
	}
}

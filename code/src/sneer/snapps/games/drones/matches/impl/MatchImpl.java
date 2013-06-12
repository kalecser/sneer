package sneer.snapps.games.drones.matches.impl;

import static basis.environments.Environments.my;
import sneer.snapps.games.drones.matches.Match;
import sneer.snapps.games.drones.units.Unit;
import sneer.snapps.games.drones.units.Units;

class MatchImpl implements Match {

	private final Unit unit1 = my(Units.class).create(  0, Unit.Direction.RIGHT, "Player 1");
	private final Unit unit2 = my(Units.class).create(700, Unit.Direction.LEFT,  "Player 2");

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

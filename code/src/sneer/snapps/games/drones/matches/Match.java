package sneer.snapps.games.drones.matches;

import sneer.snapps.games.drones.units.Unit;
import basis.brickness.Brick;

@Brick
public interface Match {

	void step();

	Unit unit1();

	Unit unit2();

	boolean isOver();

	String result();
}

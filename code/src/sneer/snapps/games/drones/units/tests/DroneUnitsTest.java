package sneer.snapps.games.drones.units.tests;

import static basis.environments.Environments.my;
import static sneer.snapps.games.drones.units.Unit.Direction.LEFT;
import static sneer.snapps.games.drones.units.Unit.Direction.RIGHT;

import org.junit.Test;

import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.snapps.games.drones.units.Unit;
import sneer.snapps.games.drones.units.Units;

public class DroneUnitsTest extends BrickTestBase {

	private final Units subject = my(Units.class);

	@Test
	public void units() {
		Unit unit = subject.create(100, RIGHT, "Player 1");
		unit.move();

		assertEquals(110, unit.x());
	}

	@Test
	public void collision() {
		Unit unit1 = subject.create(100, RIGHT, "Player 1");
		Unit unit2 = subject.create(209, LEFT, "Player 2");

		assertFalse(unit1.collidesWith(unit2));

		unit2.move();

		assertTrue(unit1.collidesWith(unit2));
	}
}

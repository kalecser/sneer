package sneer.snapps.games.drones.units.tests;

import static basis.environments.Environments.my;
import static sneer.snapps.games.drones.units.Unit.Direction.LEFT;
import static sneer.snapps.games.drones.units.Unit.Direction.RIGHT;
import static sneer.snapps.games.drones.units.UnitAttribute.ARMOR;
import static sneer.snapps.games.drones.units.UnitAttribute.LIFE;
import static sneer.snapps.games.drones.units.UnitAttribute.STRENGTH;

import org.junit.Test;

import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.snapps.games.drones.units.Unit;
import sneer.snapps.games.drones.units.Units;

public class DroneUnitTest extends BrickTestBase {

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

	@Test
	public void attributes() {
		Unit unit = subject.create(100, RIGHT, "Player 1");

		unit.define(LIFE, 400);
		unit.define(STRENGTH, 350);
		unit.define(ARMOR, 250);

		assertEquals(unit.getAttribute(LIFE), 400);
		assertEquals(unit.getAttribute(STRENGTH), 350);
		assertEquals(unit.getAttribute(ARMOR), 250);
	}
}

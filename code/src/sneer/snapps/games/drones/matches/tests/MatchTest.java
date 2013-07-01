package sneer.snapps.games.drones.matches.tests;

import static basis.environments.Environments.my;
import static sneer.snapps.games.drones.units.UnitAttribute.ARMOR;
import static sneer.snapps.games.drones.units.UnitAttribute.HITPOINTS;
import static sneer.snapps.games.drones.units.UnitAttribute.STRENGTH;

import org.junit.Test;

import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.snapps.games.drones.matches.Match;
import sneer.snapps.games.drones.units.Unit;

public class MatchTest extends BrickTestBase {

	private final Match subject = my(Match.class);

	@Test(timeout=1000)
	public void combat() {
		Unit unit1 = subject.unit1();
		unit1.set(HITPOINTS, 400);
		unit1.set(STRENGTH, 200);
		unit1.set(ARMOR, 200);

		Unit unit2 = subject.unit2();
		unit2.set(HITPOINTS, 400);
		unit2.set(STRENGTH, 100);
		unit2.set(ARMOR, 300);
		
		while (!unit1.collidesWith(unit2))
			subject.step();
		
		assertFloat(320, unit1.hitpoints());
		assertFloat(260, unit2.hitpoints());
	}
	
	@Test(timeout=1000)
	public void death() {
		Unit unit1 = subject.unit1();
		unit1.set(HITPOINTS, 400);
		unit1.set(STRENGTH, 200);
		unit1.set(ARMOR, 200);

		Unit unit2 = subject.unit2();
		unit2.set(HITPOINTS, 400);
		unit2.set(STRENGTH, 100);
		unit2.set(ARMOR, 300);

		while (!subject.isOver())
			subject.step();

		assertEquals("Player 1 wins!", subject.result());
	}
	
	@Test(timeout=1000)
	public void draw() {
		Unit unit1 = subject.unit1();
		unit1.set(HITPOINTS, 1);
		unit1.set(STRENGTH, 1);
		unit1.set(ARMOR, 1);
		
		Unit unit2 = subject.unit2();
		unit2.set(HITPOINTS, 1);
		unit2.set(STRENGTH, 1);
		unit2.set(ARMOR, 1);
		
		while (!subject.isOver())
			subject.step();
		
		assertEquals("Draw!", subject.result());
	}
	
	
}

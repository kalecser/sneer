package sneer.bricks.snapps.games.go.tests.logic;

import org.junit.Test;

import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;
import sneer.bricks.snapps.games.go.impl.logic.IllegalMove;
import sneer.bricks.snapps.games.go.impl.logic.Intersection;
import sneer.bricks.snapps.games.go.impl.logic.IntersectionUtils;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;


public class IntersectionUtilsTest extends BrickTestBase {

	@Test
	public void twoIntersectionGroupsWithSameStones_shouldBeSameSituation(){
		int size = 5;
		final Intersection[][] intersectionsA = IntersectionUtils.createIntersections(size);
		final Intersection[][] intersectionsB = IntersectionUtils.createIntersections(size);
		assertTrue(IntersectionUtils.sameSituation(intersectionsA, intersectionsB));
	}
	
	@Test
	public void twoIntersectionGroupsWithDifferentStones_shouldNotBeSameSituation() throws IllegalMove{
		int size = 5;
		final Intersection[][] intersectionsA = IntersectionUtils.createIntersections(size);
		final Intersection[][] intersectionsB = IntersectionUtils.createIntersections(size);
		intersectionsB[2][2].setStone(StoneColor.BLACK);
		assertFalse(IntersectionUtils.sameSituation(intersectionsA, intersectionsB));
	}
	
	@Test
	public void anIntersectionCopy_shouldBeSameSituation(){
		int size = 5;
		final Intersection[][] intersectionsA = IntersectionUtils.createIntersections(size);
		final Intersection[][] intersectionsB = IntersectionUtils.copy(intersectionsA);
		assertTrue(IntersectionUtils.sameSituation(intersectionsA, intersectionsB));
	}
	
	@Test
	public void anIntersectionCopyIsModifed_shouldNotBeSameSituation() throws IllegalMove{
		int size = 5;
		final Intersection[][] intersectionsA = IntersectionUtils.createIntersections(size);
		final Intersection[][] intersectionsB = IntersectionUtils.copy(intersectionsA);
		intersectionsB[2][2].setStone(StoneColor.BLACK);
		assertFalse(IntersectionUtils.sameSituation(intersectionsA, intersectionsB));
	}
}

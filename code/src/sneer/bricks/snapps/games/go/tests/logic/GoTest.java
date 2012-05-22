package sneer.bricks.snapps.games.go.tests.logic;

import org.junit.Test;

import sneer.bricks.snapps.games.go.logic.GoBoard;
import sneer.bricks.snapps.games.go.logic.ToroidalGoBoard;
import sneer.bricks.snapps.games.go.logic.GoBoard.StoneColor;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class GoTest extends BrickTestBase {

	private GoBoard _board;
	

	private void assertScore(int black, int white) {
		assertSame(black, _board.blackScore().currentValue());
		assertSame(white, _board.whiteScore().currentValue());
	}

	@Test
	public void testSingleStoneCapture() {
		_board = new ToroidalGoBoard(9);
		
		_board.playStone(4, 2);
		_board.playStone(4, 3);
		_board.playStone(3, 3);
		_board.playStone(3, 4);
		_board.playStone(5, 3);
		_board.playStone(5, 4);

		assertNotNull(_board.stoneAt(4, 3));
		_board.playStone(4,4);
		assertNull(   _board.stoneAt(4, 3));
	}

	@Test
	public void testBigGroupCapture() {
		String[] setup = new String[]{
			    "+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + x x + + +",
				"+ + + x o o x + +",
				"+ + + + x o x + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		
		_board.playStone(5, 5);
		
		assertEquals(
		    " + + + + + + + + +\n" +
			" + + + + + + + + +\n" +
			" + + + + x x + + +\n" +
			" + + + x + + x + +\n" +
			" + + + + x + x + +\n" +
			" + + + + + x + + +\n" +
			" + + + + + + + + +\n" +
			" + + + + + + + + +\n" +
			" + + + + + + + + +\n",
			_board.printOut()
		);
		
		assertScore(3, 0);
	}
	
	@Test
	public void testSuicide() {
		String[] setup = new String[] {
			    "+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + o o + + +",
				"+ + + o x x o + +",
				"+ + + + o + o + +",
				"+ + + + + o + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		assertFalse(_board.canPlayStone(5, 4));
		assertTrue(_board.stoneAt(5, 4) == null);
	}
	
	@Test
	public void testKillOtherFirst() {
		String[] setup = new String[]{
			    "+ + + + + + + + +",
				"+ + + + x + + + +",
				"+ + + x o x + + +",
				"+ + + o + o + + +",
				"+ + + + o + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		assertTrue(_board.canPlayStone(4, 3));
	}
	
	@Test
	public void testKo() {
		String[] setup = new String[]{
			    "+ + + + + + + + +",
				"+ + + + x + + + +",
				"+ + + x o x + + +",
				"+ + + o + o + + +",
				"+ + + + o + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		assertTrue(_board.canPlayStone(4, 3));
		_board.playStone(4, 3);
		assertFalse(_board.canPlayStone(4, 2));
	}

	
	@Test
	public void testMultipleGroupKill() {
		String[] setup = new String[]{
			    "+ + + + + + + + +",
				"+ + + + x + + + +",
				"+ + + x o x + + +",
				"+ + x o + + + + +",
				"+ + + x + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		
		_board.playStone(4, 3);
		assertEquals(_board.printOut(),
			 	" + + + + + + + + +\n"+
				" + + + + x + + + +\n"+
				" + + + x + x + + +\n"+
				" + + x + x + + + +\n"+
				" + + + x + + + + +\n"+
				" + + + + + + + + +\n"+
				" + + + + + + + + +\n"+
				" + + + + + + + + +\n"+
				" + + + + + + + + +\n"
		);
		assertScore(2, 0);
	}

	@Test
	public void testPass() {
		ToroidalGoBoard subject = new ToroidalGoBoard(new String[]{});
		assertSame(StoneColor.BLACK, subject.nextToPlay());
		subject.passTurn();
		assertSame(StoneColor.WHITE, subject.nextToPlay());
	}

	@Test
	public void testEndByPass() {
		ToroidalGoBoard subject = new ToroidalGoBoard(new String[]{});
		assertSame(StoneColor.BLACK, subject.nextToPlay());
		subject.passTurn();
		assertSame(StoneColor.WHITE, subject.nextToPlay());
		subject.passTurn();
		assertNull(subject.nextToPlay());
	}
	
	@Test
	public void testResign() {
		ToroidalGoBoard subject = new ToroidalGoBoard(new String[]{});
		subject.resign();
		assertNull(subject.nextToPlay());
		assertSame(StoneColor.WHITE, subject.winner().currentValue());
	}

	@Test
	public void testSingleStoneCaptureScore() {
		String[] setup = new String[]{
			    "+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + x + + + +",
				"+ + + x o x + + +",
				"+ + + o + o + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		
		assertTrue(_board.stoneAt(4, 3) != null);
		_board.playStone(4,4);
		assertTrue(_board.stoneAt(4, 3) == null);
		assertScore(1, 0);
		
		_board.playStone(4,5);
		_board.playStone(0,1);
		
		assertTrue(_board.stoneAt(4, 4) != null);
		_board.playStone(4,3);
		assertTrue(_board.stoneAt(4, 4) == null);
		assertScore(1, 1);
	}
	
	@Test
	public void testScore() {
		String[] setup = new String[]{
			    "+ + + + + + + + +",
				"+ + + + x + + + +",
				"+ + + x + x + + +",
				"+ + x + x + + + +",
				"+ + + x + + + + +",
				"+ + + + o o o + +",
				"+ + + + o + o + +",
				"+ + + + + o + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		_board.passTurn();
		_board.passTurn();
		assertScore(2, 1);
	}


	@Test
	public void deadGroup() {
		String[] setup = new String[]{
			    "+ + + + + + + + +",
				"+ + + + x x + + +",
				"+ + + x + + x + +",
				"+ + x + + + o x +",
				"+ + + x + o o x +",
				"+ + + + x o x + +",
				"+ + + + x x x + +",
				"+ o + + + + + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		_board.passTurn();
		_board.passTurn();
		_board.toggleDeadStone(5, 4);
		assertScore(14, 0);
		
		setup = new String[]{
			    "+ + + + + + + + +",
				"+ + + + x x + + +",
				"+ x x x + o x + +",
				"x o + o + o o x +",
				"+ x x x + o o x +",
				"+ + + + x o x + +",
				"+ + o + x x x + +",
				"+ o + o + + + + +",
				"+ + o + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		_board.passTurn();
		_board.passTurn();
		_board.toggleDeadStone(5, 4);
		assertScore(20, 1);
	}

	@Test(timeout = 2000)
	public void deadGroupMisclickOnFreeIntersectionDoesNotFreeze() {
		String[] setup = new String[]{
			    "+ + + + + + + + +",
				"+ + + + x x + + +",
				"+ + + x + + x + +",
				"+ + x + + + o x +",
				"+ + + x + o o x +",
				"+ + + + x o x + +",
				"+ + + + x x x + +",
				"+ o + + + + + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		_board.passTurn();
		_board.passTurn();
		_board.toggleDeadStone(0, 0);
	}

	@Test
	public void untoggleDeadGroup() {
		String[] setup = new String[]{
			    "+ + + + + + + + +",
				"+ + x x + + + + +",
				"+ x o + x + + + +",
				"+ + x x + + + + +",
				"+ x o + x + + + +",
				"+ + x x + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + o + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		_board.passTurn();
		_board.passTurn();
		_board.toggleDeadStone(2, 2);
		assertScore(3, 0);
		_board.toggleDeadStone(2, 4);
		assertScore(6, 0);
		_board.toggleDeadStone(2, 4);
		assertScore(3, 0);
	}
	
}

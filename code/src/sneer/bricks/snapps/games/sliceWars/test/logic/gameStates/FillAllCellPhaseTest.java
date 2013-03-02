package sneer.bricks.snapps.games.sliceWars.test.logic.gameStates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.awt.Polygon;

import org.junit.Test;

import sneer.bricks.snapps.games.sliceWars.impl.logic.BoardCell;
import sneer.bricks.snapps.games.sliceWars.impl.logic.BoardCellImpl;
import sneer.bricks.snapps.games.sliceWars.impl.logic.Player;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.FillAllCell;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.GameState;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.GameStateContextImpl.Phase;


public class FillAllCellPhaseTest {
	
	@Test
	public void testState(){
		
		Polygon irrelevant = new Polygon();
		final BoardCell p1Cell = new BoardCellImpl(irrelevant, null);
		final BoardCell p2Cell = new BoardCellImpl(irrelevant, null);
		final BoardCell p3Cell = new BoardCellImpl(irrelevant, null);
		
		final int boardCellCount = 3;
		assertTrue(Player.EMPTY.equals(p1Cell.getOwner()));
		assertTrue(Player.EMPTY.equals(p2Cell.getOwner()));
		GameState subject = new FillAllCell(new Player(1, 2), new BoardMockAdapter() {
			
			@Override
			public boolean isFilled() {
				boolean cell1Filled = !Player.EMPTY.equals(p1Cell.getOwner());
				boolean cell2Filled = !Player.EMPTY.equals(p2Cell.getOwner());
				boolean cell3Filled = !Player.EMPTY.equals(p3Cell.getOwner());
				return cell1Filled  && cell2Filled && cell3Filled;
			}
			
			@Override
			public BoardCell getCellAtOrNull(int x, int y) {
				if(x == 0) return p1Cell;
				if(x == 1) return p2Cell;
				return p3Cell;
			}

			@Override
			public int getCellCount() {
				return boardCellCount;
			}
		});
		GameStateContextMock gameStateContextMock = new GameStateContextMock();
		subject.play(0, 0,gameStateContextMock);
		assertTrue(Player.PLAYER1.equals(p1Cell.getOwner()));
		subject.play(1, 0,gameStateContextMock);
		assertTrue(Player.PLAYER2.equals(p2Cell.getOwner()));
		subject.play(2, 0, gameStateContextMock);
		assertNotSame(Player.PLAYER3, p2Cell.getOwner());
		assertEquals(Phase.FIRST_DICE_DISTRIBUTION,gameStateContextMock.getPhase());
		assertEquals(Player.PLAYER1, gameStateContextMock.getWhoIsPlaying());
	}

}

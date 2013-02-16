package spikes.lucass.sliceWars.test.logic.gameStates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Polygon;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.BoardCellImpl;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.FillAllCellPhase;
import spikes.lucass.sliceWars.src.logic.gameStates.FirstDiceBatchDistribution;
import spikes.lucass.sliceWars.src.logic.gameStates.GameState;


public class FillAllCellPhaseTest {
	
	@Test
	public void testState(){
		
		Polygon irrelevant = new Polygon();
		final BoardCell p1Cell = new BoardCellImpl(irrelevant);
		final BoardCell p2Cell = new BoardCellImpl(irrelevant);
		final BoardCell p3Cell = new BoardCellImpl(irrelevant);
		
		final int boardCellCount = 3;
		assertTrue(Player.EMPTY.equals(p1Cell.getOwner()));
		assertTrue(Player.EMPTY.equals(p2Cell.getOwner()));
		FillAllCellPhase subject = new FillAllCellPhase(new Player(1, 2), new BoardMockAdapter() {
			
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
		subject.play(0, 0);
		assertTrue(Player.PLAYER1.equals(p1Cell.getOwner()));
		subject.play(1, 0);
		assertTrue(Player.PLAYER2.equals(p2Cell.getOwner()));
		GameState play = subject.play(2, 0);
		assertFalse(Player.PLAYER3.equals(p2Cell.getOwner()));
		assertTrue(play instanceof FirstDiceBatchDistribution);
		assertEquals(Player.PLAYER1, play.getWhoIsPlaying());
	}

}

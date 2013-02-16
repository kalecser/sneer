package spikes.lucass.sliceWars.test.logic.gameStates;

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
		final BoardCellImpl boardCell = new BoardCellImpl(new Polygon());
		assertTrue(Player.EMPTY.equals(boardCell.getOwner()));
		FillAllCellPhase subject = new FillAllCellPhase(new Player(1, 1), new BoardMockAdapter() {
			
			@Override
			public boolean isFilled() {
				return true;
			}
			
			@Override
			public BoardCell getCellAtOrNull(int x, int y) {
				return boardCell;
			}

			@Override
			public int getCellCount() {
				return 0;
			}
		});
		GameState play = subject.play(0, 0);
		assertFalse(Player.EMPTY.equals(boardCell.getOwner()));
		assertTrue(play instanceof FirstDiceBatchDistribution);
	}

}

package spikes.lucass.sliceWars.test.logic.gameStates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Polygon;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.FillAllCellPhase;
import spikes.lucass.sliceWars.src.logic.gameStates.FirstDiceBatchDistribution;
import spikes.lucass.sliceWars.src.logic.gameStates.GameState;


public class FillAllCellPhaseTest {
	
	@Test
	public void testState(){
		final BoardCell boardCell = new BoardCell(new Polygon());
		assertTrue(Player.Empty.equals(boardCell.getOwner()));
		FillAllCellPhase subject = new FillAllCellPhase(new Board() {
			
			@Override
			public boolean isFilled() {
				return true;
			}
			
			@Override
			public BoardCell getCellAtOrNull(int x, int y) {
				return boardCell;
			}
			
			@Override
			public Set<BoardCell> getBoardCells() {
				return new LinkedHashSet<BoardCell>();
			}
		});
		GameState play = subject.play(0, 0);
		assertFalse(Player.Empty.equals(boardCell.getOwner()));
		assertTrue(play instanceof FirstDiceBatchDistribution);
	}

}

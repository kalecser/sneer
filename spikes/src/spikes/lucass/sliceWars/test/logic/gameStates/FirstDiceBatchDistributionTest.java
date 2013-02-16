package spikes.lucass.sliceWars.test.logic.gameStates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Polygon;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.FirstAttackPhase;
import spikes.lucass.sliceWars.src.logic.gameStates.FirstDiceBatchDistribution;
import spikes.lucass.sliceWars.src.logic.gameStates.GameState;


public class FirstDiceBatchDistributionTest {
	
	@Test
	public void testState(){
		final BoardCell boardCell = new BoardCell(new Polygon());
		boardCell.setOwner(Player.PLAYER1);
		boardCell.setDiceCount(1);
		final int boardCellCount = 1;
		FirstDiceBatchDistribution subject = new  FirstDiceBatchDistribution(new Player(1, 1),new Board() {
			
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

			@Override
			public int getCellCount() {
				return boardCellCount;
			}
		});
		GameState play = subject.play(0, 0);
		assertEquals(2, boardCell.getDiceCount());
		assertTrue(play instanceof FirstAttackPhase);
	}

	
}

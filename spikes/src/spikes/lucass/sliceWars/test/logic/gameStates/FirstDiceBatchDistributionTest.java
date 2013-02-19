package spikes.lucass.sliceWars.test.logic.gameStates;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.FirstDiceBatchDistribution;
import spikes.lucass.sliceWars.src.logic.gameStates.GameState;


public class FirstDiceBatchDistributionTest {
	
	@Test
	public void testState(){
		final BoardCellMock p1Cell = new BoardCellMock(Player.PLAYER1);
		final BoardCellMock p2Cell = new BoardCellMock(Player.PLAYER2);
		
		final int boardCellCount = 4;
		FirstDiceBatchDistribution subject = new  FirstDiceBatchDistribution(new Player(1, 2),new BoardMockAdapter() {
			
			@Override
			public BoardCell getCellAtOrNull(int x, int y) {
				if(x == 0) return p1Cell;
				return p2Cell;
			}

			@Override
			public int getCellCount() {
				return boardCellCount;
			}
		});
		subject.play(0, 0);
		assertEquals(1, p1Cell.getDiceCount());
		subject.play(0, 0);
		assertEquals(2, p1Cell.getDiceCount());
		subject.play(0, 0);
		assertEquals(2, p1Cell.getDiceCount());
		subject.play(1, 0);
		assertEquals(1, p2Cell.getDiceCount());
		GameState phase = subject.play(1, 0);
		assertEquals(2, p2Cell.getDiceCount());
		assertEquals(GameState.Phase.FIRST_ATTACK,phase.getPhase());
		assertEquals(phase.getWhoIsPlaying(), Player.PLAYER1);
	}

	
}

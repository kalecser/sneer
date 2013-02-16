package spikes.lucass.sliceWars.test.logic.gameStates;

import static org.junit.Assert.fail;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.DistributeDiePhase;

public class DistributeDiePhaseTest {

	@Test
	public void testState(){
		final BoardCellMock attacker = new BoardCellMock(Player.PLAYER1);
		final BoardCellMock defender = new BoardCellMock(Player.PLAYER2);
		
		final int boardCellCount = 2;
		DistributeDiePhase subject = new DistributeDiePhase(new Player(1, 2),new Board() {
			
			@Override
			public boolean isFilled() {
				return true;
			}
			
			@Override
			public BoardCell getCellAtOrNull(int x, int y) {
				if(x == 0) return attacker;
				return defender;
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
		fail(subject.toString());
	}
}

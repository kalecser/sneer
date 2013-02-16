package spikes.lucass.sliceWars.test.logic.gameStates;

import static org.junit.Assert.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.AttackPhase;
import spikes.lucass.sliceWars.src.logic.gameStates.DistributeDiePhase;
import spikes.lucass.sliceWars.src.logic.gameStates.GameState;

public class AttackPhaseTest {

	@Test
	public void testState(){
		final BoardCellMock attacker = new BoardCellMock(Player.PLAYER1);
		final BoardCellMock defender = new BoardCellMock(Player.PLAYER2);
		
		final int boardCellCount = 2;
		AttackPhase subject = new AttackPhase(new Player(1, 2),new Board() {
			
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
		subject.play(0, 0);
		subject.play(1, 0);
		assertTrue(defender.wasAttacked());
		GameState nextPhase = subject.pass();
		assertTrue(nextPhase instanceof DistributeDiePhase);
	}
	
}

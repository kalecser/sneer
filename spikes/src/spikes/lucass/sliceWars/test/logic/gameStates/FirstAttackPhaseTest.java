package spikes.lucass.sliceWars.test.logic.gameStates;

import static org.junit.Assert.assertTrue;

import java.awt.Polygon;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.BoardCellImpl;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.DistributeDiePhase;
import spikes.lucass.sliceWars.src.logic.gameStates.FirstAttackPhase;
import spikes.lucass.sliceWars.src.logic.gameStates.GameState;

public class FirstAttackPhaseTest {

	@Test
	public void testState(){
		final BoardCellImpl boardCell = new BoardCellImpl(new Polygon());
		boardCell.setOwner(Player.PLAYER1);
		boardCell.setDiceCount(1);
		final int boardCellCount = 1;
		FirstAttackPhase subject = new  FirstAttackPhase(new Player(1, 2),new BoardMockAdapter() {
			
			@Override
			public BoardCell getCellAtOrNull(int x, int y) {
				return boardCell;
			}

			@Override
			public int getCellCount() {
				return boardCellCount;
			}
		});
		GameState play = subject.play(0, 0);
		assertTrue(play instanceof FirstAttackPhase);
		subject.pass();
		play = subject.play(0, 0);
		assertTrue(play instanceof FirstAttackPhase);
		GameState afterPass = subject.pass();
		assertTrue(afterPass instanceof DistributeDiePhase);
	}
	
}

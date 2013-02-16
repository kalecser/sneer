package spikes.lucass.sliceWars.test.logic.gameStates;

import static org.junit.Assert.assertTrue;

import java.awt.Polygon;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.BoardCellImpl;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.AttackPhase;
import spikes.lucass.sliceWars.src.logic.gameStates.DistributeDiePhase;
import spikes.lucass.sliceWars.src.logic.gameStates.FirstAttackPhase;
import spikes.lucass.sliceWars.src.logic.gameStates.GameState;

public class AttackPhaseTest {

	@Test
	public void testState(){
		final BoardCellImpl attacker = new BoardCellImpl(new Polygon());
		attacker.setOwner(Player.PLAYER1);
		attacker.setDiceCount(1);
		
		final BoardCellImpl defender = new BoardCellImpl(new Polygon());
		defender.setOwner(Player.PLAYER2);
		defender.setDiceCount(1);
		
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
//		GameState play = subject.play(0, 0);
//		assertTrue(play instanceof FirstAttackPhase);
//		subject.pass();
//		play = subject.play(0, 0);
//		assertTrue(play instanceof FirstAttackPhase);
//		GameState afterPass = subject.pass();
//		assertTrue(afterPass instanceof DistributeDiePhase);
	}
	
}

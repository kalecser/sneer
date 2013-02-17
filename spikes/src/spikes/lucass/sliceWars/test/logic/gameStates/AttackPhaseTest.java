package spikes.lucass.sliceWars.test.logic.gameStates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.AttackPhase;
import spikes.lucass.sliceWars.src.logic.gameStates.DistributeDiePhase;
import spikes.lucass.sliceWars.src.logic.gameStates.GameEnded;
import spikes.lucass.sliceWars.src.logic.gameStates.GameState;

public class AttackPhaseTest {

	@Test
	public void testState(){
		final BoardCellMock attacker = new BoardCellMock(Player.PLAYER1);
		final BoardCellMock defender = new BoardCellMock(Player.PLAYER2);
		
		final AtomicBoolean linked = new AtomicBoolean(false);
		
		final int boardCellCount = 2;
		AttackPhase subject = new AttackPhase(new Player(1, 2),new BoardMockAdapter() {
			
			@Override
			public BoardCell getCellAtOrNull(int x, int y) {
				if(x == 0) return attacker;
				return defender;
			}

			@Override
			public int getCellCount() {
				return boardCellCount;
			}
			
			@Override
			public boolean areLinked(BoardCell c1, BoardCell c2) {
				return linked.get();
			}
			
			@Override
			public int getBiggestLinkedCellCountForPlayer(Player player) {
				return 1;
			}
		});
		subject.play(0, 0);
		subject.play(1, 0);
		assertTrue(!defender.wasAttacked());
		linked.set(true);
		subject.play(0, 0);
		subject.play(1, 0);
		assertTrue(defender.wasAttacked());
		
		GameState nextPhase = subject.pass();
		assertTrue(nextPhase instanceof DistributeDiePhase);
	}
	
	@Test
	public void allCellsFilled_ShouldSkipDistributeDie(){
		final BoardCellMock defender = new BoardCellMock(Player.PLAYER1);
		
		AttackPhase subject = new AttackPhase(new Player(1, 2),new BoardMockAdapter() {
			
			@Override
			public BoardCell getCellAtOrNull(int x, int y) {
				return defender;
			}
			
			@Override
			public boolean areaAllCellsFilled(Player currentPlaying) {
				return true;
			}
		});
		assertEquals(Player.PLAYER1, subject.getWhoIsPlaying());
		GameState nextPhase = subject.pass();
		assertTrue(nextPhase instanceof AttackPhase);
		assertEquals(Player.PLAYER2, nextPhase.getWhoIsPlaying());
	}
	
	@Test
	public void nextPlayerHasNoCells_ShouldSkipPlayerOnDistributeDie(){
		final BoardCellMock defender = new BoardCellMock(Player.PLAYER1);
		
		AttackPhase subject = new AttackPhase(new Player(1, 3),new BoardMockAdapter() {
			
			@Override
			public BoardCell getCellAtOrNull(int x, int y) {
				return defender;
			}
			
			@Override
			public boolean areaAllCellsFilled(Player currentPlaying) {
				return false;
			}
			
			@Override
			public int getBiggestLinkedCellCountForPlayer(Player player) {
				if(player.equals(Player.PLAYER1)) return 1;
				if(player.equals(Player.PLAYER3)) return 1;
				return 0;
			}
		});
		assertEquals(Player.PLAYER1, subject.getWhoIsPlaying());
		GameState nextPhase = subject.pass();
		assertTrue(nextPhase instanceof DistributeDiePhase);
		assertEquals(Player.PLAYER3, nextPhase.getWhoIsPlaying());
	}
	
	@Test
	public void afterAttack_gameEnded(){	
		final BoardCellMock attacker = new BoardCellMock(Player.PLAYER1);
		final BoardCellMock defender = new BoardCellMock(Player.PLAYER2);
		
		AttackPhase subject = new AttackPhase(new Player(1, 2),new BoardMockAdapter() {
			@Override
			public BoardCell getCellAtOrNull(int x, int y) {
				if(x == 0) return attacker;
				return defender;
			}
			
			@Override
			public boolean areaAllCellsFilled(Player currentPlaying) {
				return false;
			}
			
			@Override
			public int getBiggestLinkedCellCountForPlayer(Player player) {
				if(player.equals(Player.PLAYER1)) return 1;
				return 0;
			}
		});
		subject.play(0, 0);
		GameState nextPhase = subject.play(1, 0);
		assertTrue(nextPhase instanceof GameEnded);
		assertEquals(Player.PLAYER1, nextPhase.getWhoIsPlaying());
	}
	
}

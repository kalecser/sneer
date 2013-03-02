package sneer.bricks.snapps.games.sliceWars.test.logic.gameStates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import sneer.bricks.snapps.games.sliceWars.impl.logic.BoardCell;
import sneer.bricks.snapps.games.sliceWars.impl.logic.Player;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.Attack;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.GameStateContextImpl.Phase;

public class AttackPhaseTest {

	@Test
	public void testState(){
		final BoardCellMock attacker = new BoardCellMock(Player.PLAYER1);
		final BoardCellMock defender = new BoardCellMock(Player.PLAYER2);
		
		final AtomicBoolean linked = new AtomicBoolean(false);
		
		final int boardCellCount = 2;
		Attack subject = new Attack(new Player(1, 2),new BoardMockAdapter() {
			
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
		GameStateContextMock gameStateContextMock = new GameStateContextMock();
		subject.play(0, 0,gameStateContextMock);
		subject.play(1, 0,gameStateContextMock);
		assertTrue(!defender.wasAttacked());
		linked.set(true);
		subject.play(0, 0,gameStateContextMock);
		subject.play(1, 0,gameStateContextMock);
		assertTrue(defender.wasAttacked());
		
		subject.pass(gameStateContextMock);
		assertEquals(Phase.DICE_DISTRIBUTION,gameStateContextMock.getPhase());
	}
	
	@Test
	public void allCellsFilled_ShouldSkipDistributeDie(){
		final BoardCellMock defender = new BoardCellMock(Player.PLAYER1);
		
		Attack subject = new Attack(new Player(1, 2),new BoardMockAdapter() {
			
			@Override
			public BoardCell getCellAtOrNull(int x, int y) {
				return defender;
			}
			
			@Override
			public boolean areaAllCellsFilledByPlayer(Player currentPlaying) {
				return true;
			}
		});
		assertEquals(Player.PLAYER1, subject.getWhoIsPlaying());
		GameStateContextMock gameStateContextMock = new GameStateContextMock();
		subject.pass(gameStateContextMock);
		assertEquals(Phase.ATTACK,gameStateContextMock.getPhase());
		assertEquals(Player.PLAYER2, gameStateContextMock.getWhoIsPlaying());
	}
	
	@Test
	public void nextPlayerHasNoCells_ShouldSkipPlayerOnDistributeDie(){
		final BoardCellMock defender = new BoardCellMock(Player.PLAYER1);
		
		Attack subject = new Attack(new Player(1, 3),new BoardMockAdapter() {
			
			@Override
			public BoardCell getCellAtOrNull(int x, int y) {
				return defender;
			}
			
			@Override
			public boolean areaAllCellsFilledByPlayer(Player currentPlaying) {
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
		GameStateContextMock gameStateContextMock = new GameStateContextMock();
		subject.pass(gameStateContextMock);
		assertEquals(Phase.DICE_DISTRIBUTION,gameStateContextMock.getPhase());
		assertEquals(Player.PLAYER3, gameStateContextMock.getWhoIsPlaying());
	}
	
	@Test
	public void afterAttack_gameEnded(){	
		final BoardCellMock attacker = new BoardCellMock(Player.PLAYER1);
		final BoardCellMock defender = new BoardCellMock(Player.PLAYER2);
		
		Attack subject = new Attack(new Player(1, 2),new BoardMockAdapter() {
			@Override
			public BoardCell getCellAtOrNull(int x, int y) {
				if(x == 0) return attacker;
				return defender;
			}
			
			@Override
			public boolean areaAllCellsFilledByPlayer(Player currentPlaying) {
				return false;
			}
			
			@Override
			public int getBiggestLinkedCellCountForPlayer(Player player) {
				if(player.equals(Player.PLAYER1)) return 1;
				return 0;
			}
		});
		GameStateContextMock gameStateContextMock = new GameStateContextMock();
		subject.play(0, 0,gameStateContextMock);
		subject.play(1, 0,gameStateContextMock);
		assertEquals(Phase.GAME_ENDED,gameStateContextMock.getPhase());
		assertEquals(Player.PLAYER1, gameStateContextMock.getWhoIsPlaying());
	}
	
}

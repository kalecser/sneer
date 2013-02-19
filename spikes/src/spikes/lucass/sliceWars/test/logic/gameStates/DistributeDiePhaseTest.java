package spikes.lucass.sliceWars.test.logic.gameStates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.Attack;
import spikes.lucass.sliceWars.src.logic.gameStates.DiceDistribution;
import spikes.lucass.sliceWars.src.logic.gameStates.GameState;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContext.Phase;

public class DistributeDiePhaseTest {

	@Test
	public void testState(){
		final BoardCellMock boardCellMock = new BoardCellMock(Player.PLAYER1);
		assertEquals(0, boardCellMock.getDiceCount());
		
		final int boardCellCount = 2;
		DiceDistribution subject = new DiceDistribution(new Player(1, 2),new BoardMockAdapter() {
			
			@Override
			public BoardCell getCellAtOrNull(int x, int y) {
				return boardCellMock;
			}

			@Override
			public int getCellCount() {
				return boardCellCount;
			}
			
			@Override
			public int getBiggestLinkedCellCountForPlayer(Player player) {
				return 2;
			}
		});
		subject.play(0, 0);
		assertEquals(1, boardCellMock.getDiceCount());
		GameState nextPhase = subject.play(0, 0);
		assertEquals(2, boardCellMock.getDiceCount());
		assertEquals(Phase.ATTACK,nextPhase.getPhase());
		Attack attackPhase = (Attack) nextPhase;
		assertTrue(attackPhase.getWhoIsPlaying().equals(Player.PLAYER1));
	}
	
	@Test
	public void ifAlreadMaxedAllCells_GoToNextPhase(){
		final BoardCellMock boardCellMock = new BoardCellMock(Player.PLAYER1){
			@Override
			public boolean canAddDie() {
				return false;
			}
		};
		
		final int boardCellCount = 2;
		DiceDistribution subject = new DiceDistribution(new Player(1, 1),new BoardMockAdapter() {
			
			@Override
			public BoardCell getCellAtOrNull(int x, int y) {
				return boardCellMock;
			}

			@Override
			public int getCellCount() {
				return boardCellCount;
			}
			
			@Override
			public int getBiggestLinkedCellCountForPlayer(Player player) {
				return 10;
			}
			
			@Override
			public boolean areaAllCellsFilled(Player currentPlaying) {
				return true;
			}
		});
		GameState nextPhase = subject.play(0, 0);
		assertEquals(Phase.ATTACK,nextPhase.getPhase());
	}
}

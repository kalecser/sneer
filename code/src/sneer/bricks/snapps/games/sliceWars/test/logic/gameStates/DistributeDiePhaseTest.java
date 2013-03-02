package sneer.bricks.snapps.games.sliceWars.test.logic.gameStates;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sneer.bricks.snapps.games.sliceWars.impl.logic.BoardCell;
import sneer.bricks.snapps.games.sliceWars.impl.logic.Player;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.DiceDistribution;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.GameStateContextImpl.Phase;

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
		GameStateContextMock gameStateContextMock = new GameStateContextMock();
		subject.play(0, 0,gameStateContextMock);
		assertEquals(1, boardCellMock.getDiceCount());
		subject.play(0, 0,gameStateContextMock);
		assertEquals(2, boardCellMock.getDiceCount());
		assertEquals(Phase.ATTACK,gameStateContextMock.getPhase());
		assertEquals(Player.PLAYER1,gameStateContextMock.getWhoIsPlaying());
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
			public boolean areaAllCellsFilledByPlayer(Player currentPlaying) {
				return true;
			}
		});
		GameStateContextMock gameStateContextMock = new GameStateContextMock();
		subject.play(0, 0,gameStateContextMock);
		assertEquals(Phase.ATTACK,gameStateContextMock.getPhase());
	}
}

package sneer.bricks.snapps.games.sliceWars.test.logic.gameStates;

import java.util.LinkedHashSet;
import java.util.Set;

import sneer.bricks.snapps.games.sliceWars.impl.logic.Board;
import sneer.bricks.snapps.games.sliceWars.impl.logic.BoardCell;
import sneer.bricks.snapps.games.sliceWars.impl.logic.Player;

public class BoardMockAdapter implements Board{

	@Override
	public BoardCell getCellAtOrNull(int x, int y) {
		return null;
	}

	@Override
	public boolean isFilled() {
		return true;
	}

	@Override
	public Set<BoardCell> getBoardCells() {
		return new LinkedHashSet<BoardCell>();
	}

	@Override
	public int getCellCount() {
		return 0;
	}

	@Override
	public int getBiggestLinkedCellCountForPlayer(Player player) {
		return 0;
	}

	@Override
	public boolean areLinked(BoardCell c1, BoardCell c2) {
		return true;
	}

	@Override
	public boolean areaAllCellsFilledByPlayer(Player currentPlaying) {
		return false;
	}

	@Override
	public void remove(BoardCell cell) {
	}

	@Override
	public boolean removingCellWillLeaveOrphans(BoardCell cell) {
		return false;
	}
}

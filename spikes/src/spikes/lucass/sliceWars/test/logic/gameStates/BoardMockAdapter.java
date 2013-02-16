package spikes.lucass.sliceWars.test.logic.gameStates;

import java.util.LinkedHashSet;
import java.util.Set;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.Player;

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
	public boolean areaAllCellsFilled(Player currentPlaying) {
		return false;
	}
}

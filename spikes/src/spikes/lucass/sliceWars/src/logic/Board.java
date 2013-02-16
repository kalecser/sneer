package spikes.lucass.sliceWars.src.logic;

import java.util.Set;

public interface Board {

	public BoardCell getCellAtOrNull(int x, int y);
	public boolean isFilled();
	public Set<BoardCell> getBoardCells();
	public int getCellCount();
	public int getBiggestLinkedCellCountForPlayer(Player player);

}
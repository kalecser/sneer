package spikes.lucass.sliceWars.src.logic;

import java.util.Set;

public interface Board {

	BoardCell getCellAtOrNull(int x, int y);
	boolean isFilled();
	Set<BoardCell> getBoardCells();
	int getCellCount();

}
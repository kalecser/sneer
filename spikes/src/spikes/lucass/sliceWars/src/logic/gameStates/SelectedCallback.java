package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.BoardCell;

public interface SelectedCallback {
	public void selectedOrNull(BoardCell selectedCellOrNull);
}

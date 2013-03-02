package sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates;

import sneer.bricks.snapps.games.sliceWars.impl.logic.BoardCell;

public interface SelectedCallback {
	public void selectedOrNull(BoardCell selectedCellOrNull);
}

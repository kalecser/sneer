package sneer.bricks.snapps.games.sliceWars.impl.gui.drawers;

import java.awt.Graphics2D;
import java.util.Collection;

import sneer.bricks.snapps.games.sliceWars.impl.logic.BoardCell;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.GameStateContext;

public class CellsDrawer implements Drawer {

	private CellDrawer simpleCellDrawer;
	private GameStateContext _gameContext;
	
	public CellsDrawer(GameStateContext gameContext,CellDrawer cellDrawer) {
		_gameContext = gameContext;
		simpleCellDrawer = cellDrawer;
	}
	
	@Override
	public void draw(Graphics2D g2) {
		Collection<BoardCell> boardCells = _gameContext.getBoardCells();
		for (BoardCell boardCell : boardCells) {
			simpleCellDrawer.draw(boardCell,g2);
		}
	}
	
}

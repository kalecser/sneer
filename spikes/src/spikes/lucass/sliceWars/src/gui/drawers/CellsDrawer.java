package spikes.lucass.sliceWars.src.gui.drawers;

import java.awt.Graphics2D;
import java.util.Collection;

import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContext;


public class CellsDrawer implements Drawer {

	private CellDrawer simpleCellDrawer;
	private GameStateContext _gameContext;
	
	public CellsDrawer(GameStateContext gameContext) {
		_gameContext = gameContext;
		simpleCellDrawer = new CellDrawer();
	}
	
	@Override
	public void draw(Graphics2D g2) {
		Collection<BoardCell> boardCells = _gameContext.getBoardCells();
		for (BoardCell boardCell : boardCells) {
			simpleCellDrawer.draw(boardCell,g2);
		}
	}
	
}

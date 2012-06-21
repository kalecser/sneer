package sneer.bricks.snapps.games.go.impl.gui.game.painters;

import java.awt.Color;
import java.awt.Graphics2D;

import sneer.bricks.snapps.games.go.impl.logic.GoBoard;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;


public class StonesInPlayPainter {

	private final StonePainter _stonePainter;
	private float _cellSize;

	public StonesInPlayPainter(StonePainter stonePainter,final float cellSize) {
		this._stonePainter = stonePainter;
		setBoardDimensions(cellSize);
	}

	public void setBoardDimensions(final float cellSize){
		this._cellSize = cellSize;
	}
	
	public void draw(final Graphics2D graphics, final GoBoard _board){
		int size = _board.size();
		
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
				paintStoneOnPosition(graphics,_board, x, y);		
	}

	private void paintStoneOnPosition(Graphics2D graphics,final GoBoard _board, int x, int y) {
		StoneColor color = _board.stoneAt(x, y);
		boolean transparent = false;
		boolean dead=transparent;
		if (color == null) {
			if (_board.nextToPlay()==null) {
				color = _board.getPrevColor(x, y);
				if (color==null) return;
				dead=true;
			}
			else return;
		}
		
		int cx = (int) (_cellSize*x);		
		int cy = (int) (_cellSize*y);		
	
		boolean black = (color == StoneColor.BLACK);
				
		graphics.setColor(toAwtColor(color));

		final boolean stoneAtPositionIsLastPlayedStone = _board.stoneAtPositionIsLastPlayedStone(x, y);
		_stonePainter.paintStoneOnCoordinates(graphics, cx, cy,black,transparent, dead, stoneAtPositionIsLastPlayedStone);
	}

	private Color toAwtColor(StoneColor color) {
		return color == StoneColor.BLACK? Color.black: Color.white;
	}
}
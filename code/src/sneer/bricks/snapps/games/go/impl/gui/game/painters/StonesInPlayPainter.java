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
		boolean dead=false;
		if (color == null) {
			if (_board.nextToPlay()==null) {
				color = _board.getPrevColor(x, y);
				if (color==null) return;
				dead=true;
			}
			else return;
		}
		
		float cx = _cellSize*x;		
		float cy = _cellSize*y;		
	
		graphics.setColor(toAwtColor(color));

		_stonePainter.paintStoneOnCoordinates(graphics, cx, cy, dead);
	}

	private Color toAwtColor(StoneColor color) {
		return color == StoneColor.BLACK? Color.black: Color.white;
	}
}
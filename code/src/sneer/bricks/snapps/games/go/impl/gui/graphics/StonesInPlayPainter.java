package sneer.bricks.snapps.games.go.impl.gui.graphics;

import java.awt.Color;
import java.awt.Graphics2D;

import sneer.bricks.snapps.games.go.impl.gui.GoBoardPanel;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;


public class StonesInPlayPainter {

	private final StonePainter stonePainter;
	private int _xOffsetMeasuredByPieces;
	private int _yOffsetMeasuredByPieces;

	public StonesInPlayPainter(final StonePainter stonePainter) {
		this.stonePainter = stonePainter;
	}
	
	public void draw(final Graphics2D graphics, final GoBoard _board){
		int size = _board.size();
		
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
				paintStoneOnPosition(graphics,_board, x, y, _xOffsetMeasuredByPieces, _yOffsetMeasuredByPieces);		
	}
	
	public void setOffset(int xOffsetMeasuredByPieces, int yOffsetMeasuredByPieces) {
		_xOffsetMeasuredByPieces = xOffsetMeasuredByPieces;
		_yOffsetMeasuredByPieces = yOffsetMeasuredByPieces;
	}

	private void paintStoneOnPosition(Graphics2D graphics,final GoBoard _board, int x, int y,final int _scrollX, final int _scrollY) {
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
		
		float cx = toCoordinateSmall(scrollX(x, _scrollX));		
		float cy = toCoordinateSmall(scrollY(y, _scrollY));		
	
		graphics.setColor(toAwtColor(color));
		

		stonePainter.paintStoneOnCoordinates(graphics, cx, cy, dead);
	}

	private Color toAwtColor(StoneColor color) {
		return color == StoneColor.BLACK? Color.black: Color.white;
	}
	
	private int scrollX(int x, final int _scrollX) { 
		return (x + _scrollX) % GoBoardPanel.BOARD_SIZE; 
	}
	
	private int scrollY(int y,final int _scrollY) { 
		return (y + _scrollY) % GoBoardPanel.BOARD_SIZE; 
	}
	
	private float toCoordinateSmall(int position) {
		return position * GoBoardPanel.CELL_SIZE;
	}
}
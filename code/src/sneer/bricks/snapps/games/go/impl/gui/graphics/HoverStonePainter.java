package sneer.bricks.snapps.games.go.impl.gui.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import sneer.bricks.snapps.games.go.impl.gui.GoBoardPanel;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;

public class HoverStonePainter{

	private StonePainter stonePainter;
	private int _xOffsetMeasuredByPieces;
	private int _yOffsetMeasuredByPieces;
	private int _hoverX;
	private int _hoverY;

	public HoverStonePainter(final StonePainter stonePainter) {
		this.stonePainter = stonePainter;
	}
	
	public void draw(final Graphics2D graphics, final GoBoard _board){
		if (!_board.canPlayStone(unscrollX(_hoverX,_xOffsetMeasuredByPieces), unscrollY(_hoverY, _yOffsetMeasuredByPieces))) return;

		if(_board.nextToPlay() == StoneColor.BLACK) graphics.setColor(new Color(0, 0, 0, 50));
		else graphics.setColor(new Color(255, 255, 255, 90));
			
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		stonePainter.paintStoneOnCoordinates(graphics, toCoordinateSmall(_hoverX), toCoordinateSmall(_hoverY), false);
	}

	private float toCoordinateSmall(int position) {
		return position * GoBoardPanel.CELL_SIZE;
	}
	
	private int unscrollX(int x,int _scrollX) { 
		return (GoBoardPanel.BOARD_SIZE + x - _scrollX) % GoBoardPanel.BOARD_SIZE; 
	}
	
	private int unscrollY(int y,int _scrollY) { 
		return (GoBoardPanel.BOARD_SIZE + y - _scrollY) % GoBoardPanel.BOARD_SIZE; 
	}

	public void setOffset(int xOffsetMeasuredByPieces, int yOffsetMeasuredByPieces) {
		_xOffsetMeasuredByPieces = xOffsetMeasuredByPieces;
		_yOffsetMeasuredByPieces = yOffsetMeasuredByPieces;
	}

	public void setHoverX(int hoverX) {
		_hoverX = hoverX;
	}
	
	public void setHoverY(int hoverY) {
		_hoverY = hoverY;
	}
}

package sneer.bricks.snapps.games.go.impl.gui.game.painters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import sneer.bricks.snapps.games.go.impl.logic.GoBoard;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;

public class HoverStonePainter{

	private StonePainter _stonePainter;
	private int _xOffsetMeasuredByPieces;
	private int _yOffsetMeasuredByPieces;
	private int _hoverX;
	private int _hoverY;
	private int _boardSize;
	private float _cellSize;
	
	public HoverStonePainter(StonePainter stonePainter, int bOARD_SIZE,float cELL_SIZE) {
		_stonePainter = stonePainter;
		setBoardDimensions(bOARD_SIZE, cELL_SIZE);
	}

	public void setBoardDimensions(final int boardSize, final float cellSize){
		_boardSize = boardSize;
		_cellSize = cellSize;
	}
	
	public void draw(final Graphics2D graphics, final GoBoard _board){
		if (!_board.canPlayStone(unscrollX(_hoverX,_xOffsetMeasuredByPieces), unscrollY(_hoverY, _yOffsetMeasuredByPieces))) return;

		if(_board.nextToPlay() == StoneColor.BLACK) graphics.setColor(new Color(0, 0, 0, 50));
		else graphics.setColor(new Color(255, 255, 255, 90));
			
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		_stonePainter.paintStoneOnCoordinates(graphics, toCoordinateSmall(_hoverX), toCoordinateSmall(_hoverY), false);
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

	private float toCoordinateSmall(int position) {
		return position * _cellSize;
	}
	
	private int unscrollX(int x,int _scrollX) { 
		return (_boardSize + x - _scrollX) % _boardSize; 
	}
	
	private int unscrollY(int y,int _scrollY) { 
		return (_boardSize + y - _scrollY) % _boardSize; 
	}
}

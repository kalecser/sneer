package sneer.bricks.snapps.games.go.gui.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

import sneer.bricks.snapps.games.go.GoBoard;
import sneer.bricks.snapps.games.go.GoBoard.StoneColor;
import sneer.bricks.snapps.games.go.gui.GoBoardPanel;

public class HoverStone{

	public void draw(final Graphics2D graphics, final GoBoard _board, final int _hoverX, final int _hoverY,final int _scrollX, final int _scrollY){
		if (!_board.canPlayStone(unscrollX(_hoverX,_scrollX), unscrollY(_hoverY, _scrollY))) return;

		if(_board.nextToPlay() == StoneColor.BLACK) graphics.setColor(new Color(0, 0, 0, 50));
		else graphics.setColor(new Color(255, 255, 255, 90));
			
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		paintStoneOnCoordinates(graphics, toCoordinateSmall(_hoverX), toCoordinateSmall(_hoverY), false);
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
	
	private void paintStoneOnCoordinates(Graphics2D graphics, float x, float y, boolean dead) {
		float d = GoBoardPanel.STONE_DIAMETER;
		if (dead) d*=0.6;
		
		graphics.fill(new Ellipse2D.Float(x - (d / 2), y - (d / 2), d, d));
		//wrapping
		int buffersize=(int)(GoBoardPanel.BOARD_IMAGE_SIZE+GoBoardPanel.CELL_SIZE);
		
		if (x==0) graphics.fill(new Ellipse2D.Float(buffersize - (d / 2), y - (d / 2), d, d));
		if (y==0) graphics.fill(new Ellipse2D.Float(x - (d / 2), buffersize - (d / 2), d, d));
		if (x==buffersize) graphics.fill(new Ellipse2D.Float(- (d / 2), y - (d / 2), d, d)); 
		if (y==buffersize) graphics.fill(new Ellipse2D.Float(x - (d / 2), - (d / 2), d, d)); 
		
		if (x==0 & y==0) graphics.fill(new Ellipse2D.Float(buffersize - (d / 2), buffersize  - (d / 2), d, d));
		if (x==buffersize & y==0) graphics.fill(new Ellipse2D.Float(- (d / 2), buffersize  - (d / 2), d, d));
		if (x==buffersize & y==buffersize) graphics.fill(new Ellipse2D.Float(- (d / 2), - (d / 2), d, d));
		if (x==0 & y==buffersize) graphics.fill(new Ellipse2D.Float(buffersize- (d / 2), - (d / 2), d, d));
	}
}

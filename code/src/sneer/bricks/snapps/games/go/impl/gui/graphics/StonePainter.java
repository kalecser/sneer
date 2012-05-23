package sneer.bricks.snapps.games.go.impl.gui.graphics;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import sneer.bricks.snapps.games.go.impl.gui.GoBoardPanel;

public class StonePainter {

	public void paintStoneOnCoordinates(Graphics2D graphics, float x, float y, boolean dead) {
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

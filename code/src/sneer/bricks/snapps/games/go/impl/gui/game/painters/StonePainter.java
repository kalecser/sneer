package sneer.bricks.snapps.games.go.impl.gui.game.painters;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class StonePainter {

	private float _boardImageSize;
	private float _stoneDiameter;
	
	public StonePainter(float bOARD_IMAGE_SIZE, float cELL_SIZE) {
		setBoardDimensions(bOARD_IMAGE_SIZE, cELL_SIZE); 
	}

	public void setBoardDimensions(final float boardImageSize, final float cellSize){
		_boardImageSize = boardImageSize;
		_stoneDiameter = cellSize *0.97f;
	}
	
	public void paintStoneOnCoordinates(Graphics2D graphics, float x, float y, boolean dead) {
		float d = _stoneDiameter;
		if (dead) d*=0.6;
		
		GradientPaint redtowhite = new GradientPaint(0,0,Color.RED,100, 0,Color.WHITE);
		graphics.setPaint(redtowhite);
		
		graphics.fill(new Ellipse2D.Float(x - (d / 2), y - (d / 2), d, d));
		//wrapping
		int buffersize=(int)(_boardImageSize);
		
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

package sneer.bricks.snapps.games.go.impl.gui.game.painters;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


public class ToroidalBoardImagePainter implements BoardImagePainter {

	@Override
	public void drawBoardAndSurroundings(Graphics graphics, Rectangle boardImageRectangle, final BufferedImage bufferImage) {
		final Rectangle clipBounds = graphics.getClipBounds();
		int xOffset = boardImageRectangle.x;
		while(clipBounds.intersects(boardImageRectangle)){
			while(clipBounds.intersects(boardImageRectangle)){
				graphics.drawImage(bufferImage, boardImageRectangle.x, boardImageRectangle.y, null);
				boardImageRectangle.x += boardImageRectangle.width;
			}
			boardImageRectangle.x = xOffset;
			boardImageRectangle.y += boardImageRectangle.height;
		}
	}

}

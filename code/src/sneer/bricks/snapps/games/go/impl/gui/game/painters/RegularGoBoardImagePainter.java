package sneer.bricks.snapps.games.go.impl.gui.game.painters;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class RegularGoBoardImagePainter implements BoardImagePainter {

	@Override
	public void drawBoardAndSurroundings(Graphics graphics,Rectangle boardImageRectangle, BufferedImage bufferImage) {
		final Rectangle clipBounds = graphics.getClipBounds();
		final Color oldColor = graphics.getColor();
		graphics.setColor(Color.LIGHT_GRAY);
		graphics.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
		graphics.drawImage(bufferImage, boardImageRectangle.x, boardImageRectangle.y, null);
		graphics.setColor(oldColor);
	}

}

package sneer.bricks.snapps.games.go.impl.gui.game.painters;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public interface BoardImagePainter {

	public abstract void drawBoardAndSurroundings(Graphics graphics,Rectangle boardImageRectangle, final BufferedImage bufferImage);

}
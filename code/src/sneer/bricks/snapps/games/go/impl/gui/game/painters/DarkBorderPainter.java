package sneer.bricks.snapps.games.go.impl.gui.game.painters;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;


public class DarkBorderPainter {
	
	private final int _boardWidth;

	public DarkBorderPainter(int boardWidth) {
		this._boardWidth = boardWidth;
	}

	
	public void draw(Graphics graphics) {
		final Color color = graphics.getColor();
		
		final Rectangle clipBounds = graphics.getClipBounds();
		
		graphics.setColor(new Color(0, 0, 0, 50));
		graphics.fillRect(0, 0, clipBounds.width, _boardWidth);
		graphics.fillRect(0, clipBounds.height-_boardWidth, clipBounds.width, _boardWidth);
		
		graphics.fillRect(0, _boardWidth, _boardWidth, clipBounds.height-(_boardWidth*2));
		graphics.fillRect(clipBounds.width-_boardWidth, _boardWidth, _boardWidth, clipBounds.height-(_boardWidth*2));
		
		graphics.setColor(color);
	}
}

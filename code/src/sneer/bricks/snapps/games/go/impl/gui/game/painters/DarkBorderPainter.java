package sneer.bricks.snapps.games.go.impl.gui.game.painters;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;


public class DarkBorderPainter {
	
	private final int _boardWidth;
	private final int _rightMenuWidth;

	public DarkBorderPainter(int boardWidth, int rightMenuWidth) {
		this._boardWidth = boardWidth;
		this._rightMenuWidth = rightMenuWidth;
	}
	
	public void draw(Graphics graphics) {
		final Color color = graphics.getColor();
		
		final Rectangle clipBounds = graphics.getClipBounds();
		
		graphics.setColor(new Color(0, 0, 0, 50));
		
		final int borderWidth = clipBounds.width- _rightMenuWidth;
		
		graphics.fillRect(0, 0, borderWidth, _boardWidth);
		graphics.fillRect(0, clipBounds.height-_boardWidth, borderWidth, _boardWidth);
		
		graphics.fillRect(0, _boardWidth, _boardWidth, clipBounds.height-(_boardWidth*2));
		graphics.fillRect(borderWidth-_boardWidth, _boardWidth, _boardWidth, clipBounds.height-(_boardWidth*2));
		
		paintMenuBackground(graphics,clipBounds.height , borderWidth);
		
		graphics.setColor(color);
	}

	private void paintMenuBackground(Graphics graphics, final int height, final int borderWidth) {
		graphics.setColor(new Color(0, 0, 0, 130));
		graphics.fillRect(borderWidth, 0, _rightMenuWidth, height);
	}
}

package sneer.bricks.snapps.games.go.impl.gui.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

public class BoardPainter{
	
	private static final Color BACKGROUND_COLOR = new Color(228,205,152);
	private int _boardSize;
	private float _boardImageSize;
	private float _cellSize;
	
	public BoardPainter(int bOARD_SIZE,float bOARD_IMAGE_SIZE, float cELL_SIZE) {
		setBoardDimensions( bOARD_SIZE, bOARD_IMAGE_SIZE, cELL_SIZE);
	}

	public void setBoardDimensions(final int boardSize, final float boardImageSize, final float cellSize){
		_boardSize = boardSize;
		_boardImageSize = boardImageSize;
		_cellSize = cellSize;
	}
	
	public void draw(Graphics2D buffer) {
		buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		buffer.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		paintBackground(buffer);
		paintGridSmall(buffer);
	}

	private void paintBackground(Graphics2D buffer) {
		buffer.setColor(BACKGROUND_COLOR);
		buffer.fillRect(0, 0,(int) _boardImageSize, (int)_boardImageSize);
	}
	
	private void paintGridSmall(Graphics2D buffer) {
		float c = 0;
		for(int i = 0; i <= _boardSize; i++ ) {
			buffer.setColor(Color.black);
			buffer.draw(new Line2D.Float(c, 0, c, _boardImageSize+_cellSize));
			buffer.draw(new Line2D.Float(0, c, _boardImageSize+_cellSize, c));
			c += _cellSize;
		}
	}

}

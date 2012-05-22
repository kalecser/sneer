package sneer.bricks.snapps.games.go.gui.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import sneer.bricks.snapps.games.go.gui.GoBoardPanel;

public class Board implements Drawable{

	
	private BufferedImage _bufferGrid;
	
	public Board() {
		createGridBuffer();
	}
	
	@Override
	public void draw(Graphics2D buffer) {
		buffer.setColor(new Color(0,0,0,0));
		buffer.fillRect(0, 0, GoBoardPanel.SCREEN_SIZE, GoBoardPanel.SCREEN_SIZE);
			
		
		buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		buffer.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		buffer.setColor(Color.black);
		buffer.drawImage(_bufferGrid, 0, 0, null);
	}
	
	private void paintGridSmall(Graphics2D buffer) {
		float c = 0;
		for(int i = 0; i <= GoBoardPanel.BOARD_SIZE; i++ ) {
			buffer.setColor(Color.black);
			buffer.draw(new Line2D.Float(c, 0, c, GoBoardPanel.BOARD_IMAGE_SIZE+GoBoardPanel.CELL_SIZE));
			buffer.draw(new Line2D.Float(0, c, GoBoardPanel.BOARD_IMAGE_SIZE+GoBoardPanel.CELL_SIZE, c));
			c += GoBoardPanel.CELL_SIZE;
		}
	}
	
	private void createGridBuffer() {
		_bufferGrid = new BufferedImage((int)(GoBoardPanel.BOARD_IMAGE_SIZE+GoBoardPanel.CELL_SIZE), (int)(GoBoardPanel.BOARD_IMAGE_SIZE+GoBoardPanel.CELL_SIZE), 
			      BufferedImage.TYPE_INT_ARGB);
		Graphics2D buffer = (Graphics2D)_bufferGrid.getGraphics();
		paintGridSmall(buffer);
	}

}

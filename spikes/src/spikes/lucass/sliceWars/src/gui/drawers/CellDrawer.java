package spikes.lucass.sliceWars.src.gui.drawers;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;

import spikes.lucass.sliceWars.src.logic.BoardCell;


public class CellDrawer {
	
	private final static Color[] colors = new Color[]{Color.GRAY, Color.BLUE, Color.RED, Color.ORANGE, Color.DARK_GRAY};
	private Font _font;

	public CellDrawer() {
		_font = new Font("Serif", Font.BOLD, 24);
	}
	
	public void draw(BoardCell boardCell, Graphics2D g2) {
		FontMetrics metrics = g2.getFontMetrics(_font);
		int playerNumber = boardCell.getOwner().getPlayerNumber();
		g2.setColor(colors[playerNumber]);
		g2.fill(boardCell.getPolygon());
		g2.setColor(Color.BLACK);
		g2.draw(boardCell.getPolygon());
		g2.setColor(Color.WHITE);
		Polygon polygon = boardCell.getPolygon();
		Rectangle2D bounds2d = polygon.getBounds2D();
		g2.setFont(_font);
		String text = boardCell.getDiceCount()+"";
		int stringHalfWidth = metrics.stringWidth(text)/2;
		g2.drawString(text, (int)bounds2d.getCenterX()-stringHalfWidth, (int)bounds2d.getCenterY());
	}

}

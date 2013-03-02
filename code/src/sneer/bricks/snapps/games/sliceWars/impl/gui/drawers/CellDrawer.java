package sneer.bricks.snapps.games.sliceWars.impl.gui.drawers;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;

import sneer.bricks.snapps.games.sliceWars.impl.logic.BoardCell;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.PlayListener;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.SelectedCallback;

public class CellDrawer implements SelectedCallback,PlayListener{
	
	private final static Color[] colors = new Color[]{
		Color.GRAY, 
		Color.BLUE, 
		Color.RED, 
		Color.ORANGE, 
		Color.DARK_GRAY, 
		Color.MAGENTA, 
		Color.PINK, 
		Color.GREEN, 
		Color.CYAN};
	private Font _font;
	private BoardCell _selectedCellOrNull;

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
		if(boardCell.equals(_selectedCellOrNull)){
			g2.setColor(new Color(255,255,255,100));
			g2.fill(boardCell.getPolygon());
		}
	}

	@Override
	public void selectedOrNull(BoardCell selectedCellOrNull) {
		_selectedCellOrNull = selectedCellOrNull;
	}

	@Override
	public void played() {
		_selectedCellOrNull = null;
	}

}

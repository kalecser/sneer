package sneer.bricks.snapps.games.sliceWars.impl.gui.drawers;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class PassButtonDrawer implements Drawer{

	private static final int BORDER = 10;
	private static final String PASS = "Pass";
	private Rectangle _rectangle;
	private boolean _visible;
	private Runnable _onClick;
	private Font _font;

	public PassButtonDrawer(int x, int y) {
		_rectangle = new Rectangle(x, y, 130, 100);
		_font = new Font("Serif", Font.BOLD, 24);
	}
	
	public void click(int x, int y) {
		if(_rectangle.contains(x, y)){
			_onClick.run();
		}
	}

	public void setVisible(boolean visible) {
		_visible = visible;
	}

	public void addClickListener(Runnable onClick) {
		_onClick = onClick;
	}

	@Override
	public void draw(Graphics2D g2) {
		if(_visible){
			g2.setFont(_font);
			FontMetrics metrics = g2.getFontMetrics(_font);
			int stringWidth = metrics.stringWidth(PASS);
			int stringHeigth = metrics.getHeight();
			_rectangle.width = BORDER + stringWidth + BORDER;
			_rectangle.height = stringHeigth;
			g2.setColor(Color.BLACK);
			g2.draw(_rectangle);
			g2.fill(_rectangle);
			g2.setColor(Color.ORANGE);
			g2.fill(_rectangle);
			g2.setColor(Color.BLACK);
			g2.drawString(PASS, _rectangle.x + BORDER, _rectangle.y+stringHeigth -2);
		}
	}

}

package sneer.bricks.snapps.games.sliceWars.impl.gui.drawers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.DiceLeftCallback;

public class DiceLeftDrawer implements Drawer,DiceLeftCallback{

	private int _x;
	private int _y;
	private Font _font;
	private int _diceLeft;

	public DiceLeftDrawer(int x, int y) {
		_x = x;
		_y = y;
		_font = new Font("Serif", Font.BOLD, 14);
	}

	@Override
	public void draw(Graphics2D g2) {
		if(_diceLeft > 0){
			g2.setFont(_font);
			g2.setColor(Color.BLACK);
			g2.drawString("Left: "+_diceLeft, _x, _y);
		}
	}

	@Override
	public void diceLeft(int diceLeft) {
		_diceLeft = diceLeft;
	}

}

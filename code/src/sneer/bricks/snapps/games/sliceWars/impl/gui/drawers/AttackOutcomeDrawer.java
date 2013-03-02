package sneer.bricks.snapps.games.sliceWars.impl.gui.drawers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import sneer.bricks.snapps.games.sliceWars.impl.logic.AttackOutcome;
import sneer.bricks.snapps.games.sliceWars.impl.logic.DiceThrowOutcome;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.AttackCallback;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.PlayListener;

public class AttackOutcomeDrawer implements Drawer,AttackCallback,PlayListener{

	private int _x;
	private int _y;
	private Font _font;
	private String _text = "";

	public AttackOutcomeDrawer(int x, int y) {
		_x = x;
		_y = y;
		_font = new Font("Serif", Font.BOLD, 14);
	}

	@Override
	public void draw(Graphics2D g2) {
		g2.setFont(_font);
		g2.setColor(Color.BLACK);
		g2.drawString(_text, _x, _y);
	}

	@Override
	public void attackedWithOutcome(AttackOutcome attackOutcome) {
		DiceThrowOutcome diceThrowOutcome = attackOutcome.diceThrowOutcome;
		String text = "ATK "+diceThrowOutcome.attackDice[0];
		
		for (int i = 1; i < diceThrowOutcome.attackDice.length; i++) {
			text += "+ "+diceThrowOutcome.attackDice[i];
		}
		
		text += " = " + diceThrowOutcome.attackSum+" | ";
		
		text += "DEF "+diceThrowOutcome.defenseDice[0];
		
		for (int i = 1; i < diceThrowOutcome.defenseDice.length; i++) {
			text += "+ "+diceThrowOutcome.defenseDice[i];
		}
		
		text += " = " + diceThrowOutcome.defenseSum;
		
		_text = text;
	}

	@Override
	public void played() {
		_text = "";
	}

}

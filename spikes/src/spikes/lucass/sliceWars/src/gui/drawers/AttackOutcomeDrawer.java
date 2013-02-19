package spikes.lucass.sliceWars.src.gui.drawers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import spikes.lucass.sliceWars.src.logic.AttackOutcome;
import spikes.lucass.sliceWars.src.logic.DiceThrowOutcome;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContext;

public class AttackOutcomeDrawer implements Drawer{

	private int _x;
	private int _y;
	private GameStateContext _phase;
	private Font _font;

	public AttackOutcomeDrawer(int x, int y, GameStateContext phase) {
		_x = x;
		_y = y;
		_font = new Font("Serif", Font.BOLD, 14);
		_phase = phase;
	}

	@Override
	public void draw(Graphics2D g2) {
		AttackOutcome attackOutcomeOrNull = _phase.getAttackOutcomeOrNull();
		if(attackOutcomeOrNull == null) return;
		DiceThrowOutcome diceThrowOutcome = attackOutcomeOrNull.diceThrowOutcome;
		String text = ""+diceThrowOutcome.attackDice[0];
		
		for (int i = 1; i < diceThrowOutcome.attackDice.length; i++) {
			text += "+ "+diceThrowOutcome.attackDice[i];
		}
		
		text += " = " + diceThrowOutcome.attackSum+" | ";
		
		text += ""+diceThrowOutcome.defenseDice[0];
		
		for (int i = 1; i < diceThrowOutcome.defenseDice.length; i++) {
			text += "+ "+diceThrowOutcome.defenseDice[i];
		}
		
		text += " = " + diceThrowOutcome.defenseSum;
		
		g2.setFont(_font);
		g2.setColor(Color.BLACK);
		g2.drawString(text, _x, _y);
	}

}

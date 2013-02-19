package spikes.lucass.sliceWars.src.gui.drawers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContext;

public class PhaseDescriptionDrawer implements Drawer{

	private int _x;
	private int _y;
	private GameStateContext _phase;
	private Font _font;

	public PhaseDescriptionDrawer(int x, int y, GameStateContext phase) {
		_x = x;
		_y = y;
		_font = new Font("Serif", Font.BOLD, 14);
		_phase = phase;
	}

	@Override
	public void draw(Graphics2D g2) {
		String text = _phase.getPhaseName() + " Turn: player "+_phase.getWhoIsPlaying().getPlayerNumber();
		g2.setFont(_font);
		g2.setColor(Color.BLACK);
		g2.drawString(text, _x, _y);
	}

}

package spikes.klaus.thomas.beings.gui;

import java.awt.Color;


class Food extends Being {

	private static final Being GERM = new Germ();

	@Override
	public void hit(Being[][] _world, int x, int y) {
		_world[x][y] = GERM;
	}

	@Override
	public Color color() {
		return Color.GREEN;
	}
	
}

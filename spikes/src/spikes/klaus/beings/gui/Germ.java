package spikes.klaus.beings.gui;

import java.awt.Color;
import java.util.Random;


class Germ extends Being {

	private static Random _random = new Random();
	
	@Override
	public Color color() {
		return Color.RED;
	}

	@Override
	public void step(Being[][] _world, int x, int y) {
		_world[x][y] = null;
		
		int move = _random.nextInt(3571) % 4; //Simply asking for nextInt(3) is biased. :(
		if (move == 0) x++;
		if (move == 1) x--;
		if (move == 2) y++;
		if (move == 3) y--;
		
		try {
			_world[x][y] = this;
		} catch (ArrayIndexOutOfBoundsException e) {
			// Germ dies.
		}
	}
	

}

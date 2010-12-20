package spikes.klaus.beings.gui;

import java.awt.Color;
import static spikes.klaus.beings.gui.BeingsFrame.*;

class Germ extends Being {

	private static final Medicine _MEDICINE = new Medicine();
	
	@Override
	public Color color() {
		return Color.RED;
	}

	@Override
	public void step(Being[][] _world, int x, int y) {
		_world[x][y] = null;
		
		int move = RANDOM.nextInt(3571) % 4; //Simply asking for nextInt(3) is biased. :(
		if (move == 0) x = (x + 1) % WORLD_WIDTH;
		if (move == 1) x = (x - 1 + WORLD_WIDTH) % WORLD_WIDTH;
		if (move == 2) y = (y + 1) % WORLD_HEIGHT;
		if (move == 3) y = (y - 1 + WORLD_HEIGHT) % WORLD_HEIGHT;
		
		
		if (_world[x][y] instanceof Food)
			_world[x][y] = _MEDICINE;
		else
			_world[x][y] = this;
	}
	

}

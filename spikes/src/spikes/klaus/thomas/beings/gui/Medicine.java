package spikes.klaus.thomas.beings.gui;

import java.awt.Color;


class Medicine extends Germ {

	@Override
	public Color color() {
		return Color.BLUE;
	}

	@Override
	public void step(Being[][] _world, int x, int y) {
		if (RANDOM.nextInt(1781) % 3 == 0)
			super.step(_world, x, y);
	}
	
	
	
}

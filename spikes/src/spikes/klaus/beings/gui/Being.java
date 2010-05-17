package spikes.klaus.beings.gui;

import java.awt.Color;


abstract class Being {

	abstract public Color color();

	@SuppressWarnings("unused")
	public void step(Being[][] world, int x, int y) {}

	@SuppressWarnings("unused")
	public void hit(Being[][] world, int x, int y) {}

}

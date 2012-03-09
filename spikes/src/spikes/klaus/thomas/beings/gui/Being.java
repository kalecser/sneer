package spikes.klaus.thomas.beings.gui;

import java.awt.Color;
import java.util.Random;


abstract class Being {

	static Random RANDOM = new Random();

	abstract public Color color();

	@SuppressWarnings("unused")
	public void step(Being[][] world, int x, int y) {}

	@SuppressWarnings("unused")
	public void hit(Being[][] world, int x, int y) {}

}

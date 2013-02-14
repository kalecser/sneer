package spikes.lucass.sliceWars.src.logic;

import java.util.Random;


public class DiceImpl implements Dice {

	@Override
	public int roll() {
		return new Random().nextInt(6)+1;
	}

}

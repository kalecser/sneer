package spikes.lucass.sliceWars.src.logic;

import java.util.Random;

public class DiceImpl implements Dice {
	
	private Random _random;

	public DiceImpl(final Random random) {
		_random = random;
	}
	
	@Override
	public int roll() {
		return _random.nextInt(6)+1;
	}

}

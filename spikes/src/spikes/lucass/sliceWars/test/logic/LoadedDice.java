package spikes.lucass.sliceWars.test.logic;

import spikes.lucass.sliceWars.src.logic.Dice;


public class LoadedDice implements Dice {

	private int _result;

	public LoadedDice(int result) {
		_result = result;
	}

	@Override
	public int roll() {
		return _result;
	}

}

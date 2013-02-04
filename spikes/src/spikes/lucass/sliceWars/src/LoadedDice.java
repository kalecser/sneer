package spikes.lucass.sliceWars.src;


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

package spikes.lucass.sliceWars.src;



public class DiceThrower {

	private Dice _atacker;
	private Dice _defense;

	public DiceThrower(Dice atacker, Dice defense) {
		_atacker = atacker;
		_defense = defense;
	}

	public PlayOutcome throwDieAndReturnOutcome(int diceCountAttacking, int diceCountDefending) {
		int[] attackResult = getDieResult(_atacker,diceCountAttacking);
		int[] defenseResult = getDieResult(_defense,diceCountDefending);
		return new PlayOutcome(attackResult, defenseResult);
		
	}

	private int[] getDieResult(Dice dice,int diceCount) {
		int[] result = new int[diceCount];
		for (int i = 0; i < result.length; i++) {
			result[i] = dice.roll();
		}
		return result;
	}

}

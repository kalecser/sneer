package sneer.bricks.snapps.games.sliceWars.impl.logic;

public class DiceThrowerImpl implements DiceThrower {

	private Dice _atacker;
	private Dice _defense;

	public DiceThrowerImpl(Dice atacker, Dice defense) {
		_atacker = atacker;
		_defense = defense;
	}

	@Override
	public DiceThrowOutcome throwDiceAndReturnOutcome(int diceCountAttacking, int diceCountDefending) {
		int[] attackResult = getDiceResult(_atacker,diceCountAttacking);
		int[] defenseResult = getDiceResult(_defense,diceCountDefending);
		return new DiceThrowOutcome(attackResult, defenseResult);
		
	}

	private int[] getDiceResult(Dice dice,int diceCount) {
		int[] result = new int[diceCount];
		for (int i = 0; i < result.length; i++) {
			result[i] = dice.roll();
		}
		return result;
	}

}

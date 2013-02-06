package spikes.lucass.sliceWars.test;

import spikes.lucass.sliceWars.src.DiceThrower;
import spikes.lucass.sliceWars.src.DiceThrowOutcome;


public class DiceThrowerMock  implements DiceThrower{

	private int _attackDiceResultForAllDice;
	private int _defenseDiceResultForAllDice;

	public DiceThrowerMock(int attackDiceResultForAllDice, int defenseDiceResultForAllDice) {
		_attackDiceResultForAllDice = attackDiceResultForAllDice;
		_defenseDiceResultForAllDice = defenseDiceResultForAllDice;
	}
	
	@Override
	public DiceThrowOutcome throwDiceAndReturnOutcome(int diceCountAttacking, int diceCountDefending) {
		int[] attackResults = new int[diceCountAttacking];
		for (int i = 0; i < attackResults.length; i++) {
			attackResults[i] = _attackDiceResultForAllDice;
		}
		int[] defenseResults = new int[diceCountDefending];
		for (int i = 0; i < defenseResults.length; i++) {
			defenseResults[i] = _defenseDiceResultForAllDice;
		}
		return new DiceThrowOutcome(attackResults, defenseResults);
	}

}

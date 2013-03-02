package sneer.bricks.snapps.games.sliceWars.test.logic;

import sneer.bricks.snapps.games.sliceWars.impl.logic.DiceThrowOutcome;
import sneer.bricks.snapps.games.sliceWars.impl.logic.DiceThrower;

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

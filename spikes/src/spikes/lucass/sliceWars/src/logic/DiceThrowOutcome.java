package spikes.lucass.sliceWars.src.logic;


public class DiceThrowOutcome {

	public final int attackSum;
	public final int defenseSum;
	public final int[] attackDice;
	public final int[] defenseDice;
	
	public DiceThrowOutcome(final int[] attackResults,final int[] defenseResults) {
		this.attackDice = attackResults;		
		attackSum = sum(attackResults);
		this.defenseDice = defenseResults;
		defenseSum = sum(defenseResults);
	}

	private int sum(final int[] array) {
		int sum = 0;
		for (int i = 0; i < array.length; i++) {
			sum += array[i];
		}
		return sum;
	}

	public boolean attackWins() {
		return attackSum > defenseSum;
	}
}

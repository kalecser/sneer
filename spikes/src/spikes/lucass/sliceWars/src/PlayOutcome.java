package spikes.lucass.sliceWars.src;


public class PlayOutcome {

	public final int attackSum;
	public final int defenseSum;
	public final int[] attackDie;
	public final int[] defenseDie;
	
	public PlayOutcome(final int[] attackResults,final int[] defenseResults) {
		this.attackDie = attackResults;		
		attackSum = sum(attackResults);
		this.defenseDie = defenseResults;
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

package spikes.lucass.sliceWars.src;


public class PlayOutcome {

	public final int attackSum;
	public final int defenseSum;
	public final boolean attackWins;
	
	public PlayOutcome(int attackerSum, int defenderSum, boolean attackerWins) {
		attackSum = attackerSum;
		defenseSum = defenderSum;
		attackWins = attackerWins;
	}
}

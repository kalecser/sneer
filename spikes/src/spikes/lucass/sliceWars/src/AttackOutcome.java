package spikes.lucass.sliceWars.src;


public class AttackOutcome {

	public DiceThrowOutcome diceThrowOutcome;
	public Cell attackCellAfterAttack;
	public Cell attackCellAfterDefense;

	public AttackOutcome(Cell newAttackCellAfterAttack, Cell newAttackCellAfterDefense, DiceThrowOutcome diceOutcome) {
		attackCellAfterAttack = newAttackCellAfterAttack;
		attackCellAfterDefense = newAttackCellAfterDefense;
		diceThrowOutcome = diceOutcome;
	}

}

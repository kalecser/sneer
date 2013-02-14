package spikes.lucass.sliceWars.src.logic;


public class AttackOutcome {

	public DiceThrowOutcome diceThrowOutcome;
	public Cell attackCellAfterAttack;
	public Cell defenseCellAfterAttack;

	public AttackOutcome(Cell newAttackCellAfterAttack, Cell newDefenseCellAfterAttack, DiceThrowOutcome diceOutcome) {
		attackCellAfterAttack = newAttackCellAfterAttack;
		defenseCellAfterAttack = newDefenseCellAfterAttack;
		diceThrowOutcome = diceOutcome;
	}

}

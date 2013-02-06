package spikes.lucass.sliceWars.src;



public class CellAttack {

	private DiceThrower _diceThrower;

	public CellAttack(DiceThrower diceThrower) {
		_diceThrower = diceThrower;
	}

	public AttackOutcome doAttack(Cell attacker, Cell defender) {
		DiceThrowOutcome diceOutcome = _diceThrower.throwDiceAndReturnOutcome(attacker.diceCount, defender.diceCount);
		Cell resultingAttackCell = new Cell();
		Cell resultingDefenseCell = new Cell();
		if(diceOutcome.attackWins()){
			resultingAttackCell.diceCount = 1;
			resultingAttackCell.owner = attacker.owner;
			resultingDefenseCell.diceCount = attacker.diceCount - 1;
			resultingDefenseCell.owner = attacker.owner;
		}else{
			resultingAttackCell.diceCount = 1;
			resultingAttackCell.owner = attacker.owner;
			resultingDefenseCell = defender;
		}
		AttackOutcome attackOutcome = new AttackOutcome(resultingAttackCell,resultingDefenseCell,diceOutcome);
		attackOutcome.diceThrowOutcome = diceOutcome;
		return attackOutcome;
	}

}

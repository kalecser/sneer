package spikes.lucass.sliceWars.src.logic;



public class CellAttack {

	private DiceThrower _diceThrower;

	public CellAttack(DiceThrower diceThrower) {
		_diceThrower = diceThrower;
	}

	public AttackOutcome doAttackReturnOutcomeOrNull(Cell attacker, Cell defender) {
		if(attacker.getDiceCount() <= 1) return null;
		if(attacker.equals(defender)) return null;
		if(attacker.owner.equals(defender.owner)) return null;
		DiceThrowOutcome diceOutcome = _diceThrower.throwDiceAndReturnOutcome(attacker.getDiceCount(), defender.getDiceCount());
		Cell resultingAttackCell = new Cell();
		Cell resultingDefenseCell = new Cell();
		resultingAttackCell.setDiceCount(1);
		resultingAttackCell.owner = attacker.owner;
		if(diceOutcome.attackWins()){
			resultingDefenseCell.setDiceCount(attacker.getDiceCount() - 1);
			resultingDefenseCell.owner = attacker.owner;
		}else{
			resultingDefenseCell = defender;
		}
		AttackOutcome attackOutcome = new AttackOutcome(resultingAttackCell,resultingDefenseCell,diceOutcome);
		attackOutcome.diceThrowOutcome = diceOutcome;
		return attackOutcome;
	}

}

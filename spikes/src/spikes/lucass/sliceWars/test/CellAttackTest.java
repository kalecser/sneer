package spikes.lucass.sliceWars.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import spikes.lucass.sliceWars.src.AttackOutcome;
import spikes.lucass.sliceWars.src.Cell;
import spikes.lucass.sliceWars.src.CellAttack;
import spikes.lucass.sliceWars.src.DiceThrowOutcome;



public class CellAttackTest {

	@Test
	public void cellAttack_AttackWins(){
		Cell attacker = new Cell();
		Cell defender = new Cell();
		attacker.diceCount = 4;
		int playerAttacking = Cell.PLAYER1;
		attacker.owner = playerAttacking;
		defender.diceCount = 3;
		int playerDefending = Cell.PLAYER2;
		defender.owner = playerDefending;
		int attackDiceResultForAllDice = 3;
		int defenseDiceResultForAllDice = 1;
		DiceThrowerMock diceThrowerMock = new DiceThrowerMock(attackDiceResultForAllDice,defenseDiceResultForAllDice);
		CellAttack cellAttack = new CellAttack(diceThrowerMock);
		AttackOutcome attackOutcome = cellAttack.doAttack(attacker,defender);
		DiceThrowOutcome diceThrowOutcome = attackOutcome.diceThrowOutcome;
		assertDiceThrowResults(diceThrowOutcome);
		Cell newAttackCell = attackOutcome.attackCellAfterAttack;
		assertEquals(newAttackCell.diceCount, 1);
		assertEquals(newAttackCell.owner, playerAttacking);
		Cell newDefenseCell = attackOutcome.attackCellAfterDefense;
		assertEquals(newDefenseCell.diceCount, 3);
		assertEquals(newDefenseCell.owner, playerAttacking);		
	}

	private void assertDiceThrowResults(DiceThrowOutcome diceThrowOutcome) {
		assertArrayEquals(new int[]{3,3,3,3}, diceThrowOutcome.attackDice);
		assertArrayEquals(new int[]{1,1,1}, diceThrowOutcome.defenseDice);
		assertEquals(diceThrowOutcome.attackSum, 12);
		assertEquals(diceThrowOutcome.defenseSum, 3);
	}
	
}

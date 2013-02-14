package spikes.lucass.sliceWars.test.logic;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.AttackOutcome;
import spikes.lucass.sliceWars.src.logic.Cell;
import spikes.lucass.sliceWars.src.logic.CellAttack;
import spikes.lucass.sliceWars.src.logic.DiceThrowOutcome;
import spikes.lucass.sliceWars.src.logic.Player;



public class CellAttackTest {

	@Test
	public void cellAttack_AttackWins(){
		Cell attacker = new Cell();
		Cell defender = new Cell();
		attacker.setDiceCount(4);
		Player playerAttacking = Player.Player1;
		attacker.owner = playerAttacking;
		defender.setDiceCount(3);
		Player playerDefending = Player.Player2;
		defender.owner = playerDefending;
		int attackDiceResultForAllDice = 3;
		int defenseDiceResultForAllDice = 1;
		DiceThrowerMock diceThrowerMock = new DiceThrowerMock(attackDiceResultForAllDice,defenseDiceResultForAllDice);
		CellAttack cellAttack = new CellAttack(diceThrowerMock);
		AttackOutcome attackOutcome = cellAttack.doAttackReturnOutcomeOrNull(attacker,defender);
		DiceThrowOutcome diceThrowOutcome = attackOutcome.diceThrowOutcome;
		assertDiceThrowResults(diceThrowOutcome);
		Cell newAttackCell = attackOutcome.attackCellAfterAttack;
		assertEquals(newAttackCell.getDiceCount(), 1);
		assertEquals(newAttackCell.owner, playerAttacking);
		Cell newDefenseCell = attackOutcome.defenseCellAfterAttack;
		assertEquals(newDefenseCell.getDiceCount(), 3);
		assertEquals(newDefenseCell.owner, playerAttacking);		
	}

	@Test
	public void cellAttack_AttackLoses(){
		Cell attacker = new Cell();
		Cell defender = new Cell();
		attacker.setDiceCount(4);
		Player playerAttacking = Player.Player1;
		attacker.owner = playerAttacking;
		defender.setDiceCount(3);
		Player playerDefending = Player.Player2;
		defender.owner = playerDefending;
		int attackDiceResultForAllDice = 1;
		int defenseDiceResultForAllDice = 4;
		DiceThrowerMock diceThrowerMock = new DiceThrowerMock(attackDiceResultForAllDice,defenseDiceResultForAllDice);
		CellAttack cellAttack = new CellAttack(diceThrowerMock);
		AttackOutcome attackOutcome = cellAttack.doAttackReturnOutcomeOrNull(attacker,defender);
		Cell newAttackCell = attackOutcome.attackCellAfterAttack;
		assertEquals(newAttackCell.getDiceCount(), 1);
		assertEquals(newAttackCell.owner, playerAttacking);
		Cell newDefenseCell = attackOutcome.defenseCellAfterAttack;
		assertEquals(newDefenseCell.getDiceCount(), 3);
		assertEquals(newDefenseCell.owner, playerDefending);
	}
	
	@Test
	public void cellAttack_draw_AttackLoses(){
		Cell attacker = new Cell();
		Cell defender = new Cell();
		attacker.setDiceCount(4);
		Player playerAttacking = Player.Player1;
		attacker.owner = playerAttacking;
		defender.setDiceCount(4);
		Player playerDefending = Player.Player2;
		defender.owner = playerDefending;
		int attackDiceResultForAllDice = 1;
		int defenseDiceResultForAllDice = 1;
		DiceThrowerMock diceThrowerMock = new DiceThrowerMock(attackDiceResultForAllDice,defenseDiceResultForAllDice);
		CellAttack cellAttack = new CellAttack(diceThrowerMock);
		AttackOutcome attackOutcome = cellAttack.doAttackReturnOutcomeOrNull(attacker,defender);
		Cell newAttackCell = attackOutcome.attackCellAfterAttack;
		assertEquals(newAttackCell.getDiceCount(), 1);
		assertEquals(newAttackCell.owner, playerAttacking);
		Cell newDefenseCell = attackOutcome.defenseCellAfterAttack;
		assertEquals(newDefenseCell.getDiceCount(), 4);
		assertEquals(newDefenseCell.owner, playerDefending);
	}
	
	private void assertDiceThrowResults(DiceThrowOutcome diceThrowOutcome) {
		assertArrayEquals(new int[]{3,3,3,3}, diceThrowOutcome.attackDice);
		assertArrayEquals(new int[]{1,1,1}, diceThrowOutcome.defenseDice);
		assertEquals(diceThrowOutcome.attackSum, 12);
		assertEquals(diceThrowOutcome.defenseSum, 3);
	}
	
}

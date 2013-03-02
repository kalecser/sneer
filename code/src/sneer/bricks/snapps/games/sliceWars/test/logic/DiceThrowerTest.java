package sneer.bricks.snapps.games.sliceWars.test.logic;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sneer.bricks.snapps.games.sliceWars.impl.logic.Dice;
import sneer.bricks.snapps.games.sliceWars.impl.logic.DiceThrowOutcome;
import sneer.bricks.snapps.games.sliceWars.impl.logic.DiceThrowerImpl;

public class DiceThrowerTest {
	
	@Test
	public void throwSomeLoadedDice_diceOutcomeShouldCheck(){
		Dice atacker = new  LoadedDice(3);
		Dice defense = new  LoadedDice(1);
		DiceThrowerImpl subject = new DiceThrowerImpl(atacker,defense);
		DiceThrowOutcome playOutcome = subject.throwDiceAndReturnOutcome(3,3);
		assertArrayEquals(new int[]{3,3,3}, playOutcome.attackDice);
		assertArrayEquals(new int[]{1,1,1}, playOutcome.defenseDice);
		assertEquals(playOutcome.attackSum, 9);
		assertEquals(playOutcome.defenseSum, 3);
	}
	
	@Test
	public void attackDiceSumIsBigger_AtackWins(){
		Dice atacker = new  LoadedDice(3);
		Dice defense = new  LoadedDice(1);
		DiceThrowerImpl subject = new DiceThrowerImpl(atacker,defense);
		DiceThrowOutcome playOutcome = subject.throwDiceAndReturnOutcome(3,3);
		assertEquals(playOutcome.attackWins(), true);
	}
	
	@Test
	public void diceSumDraw_DefenseWins(){
		Dice atacker = new  LoadedDice(3);
		Dice defense = new  LoadedDice(3);
		DiceThrowerImpl subject = new DiceThrowerImpl(atacker,defense);
		DiceThrowOutcome playOutcome = subject.throwDiceAndReturnOutcome(3,3);
		assertEquals(playOutcome.attackWins(), false);
	}
	
	@Test
	public void defenseDiceSumIsBigger_DefenseWins(){
		Dice atacker = new  LoadedDice(1);
		Dice defense = new  LoadedDice(3);
		DiceThrowerImpl subject = new DiceThrowerImpl(atacker,defense);
		DiceThrowOutcome playOutcome = subject.throwDiceAndReturnOutcome(3,3);
		assertEquals(playOutcome.attackWins(), false);
	}
}

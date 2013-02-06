package spikes.lucass.sliceWars.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import spikes.lucass.sliceWars.src.Dice;
import spikes.lucass.sliceWars.src.DiceThrowerImpl;
import spikes.lucass.sliceWars.src.LoadedDice;
import spikes.lucass.sliceWars.src.DiceThrowOutcome;


public class DiceThrowerTest {
	
	
	@Test
	public void diceOutcome(){
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
	public void atackWins(){
		Dice atacker = new  LoadedDice(3);
		Dice defense = new  LoadedDice(1);
		DiceThrowerImpl subject = new DiceThrowerImpl(atacker,defense);
		DiceThrowOutcome playOutcome = subject.throwDiceAndReturnOutcome(3,3);
		assertEquals(playOutcome.attackWins(), true);
	}
	
	@Test
	public void onDraw_DefenseWins(){
		Dice atacker = new  LoadedDice(3);
		Dice defense = new  LoadedDice(3);
		DiceThrowerImpl subject = new DiceThrowerImpl(atacker,defense);
		DiceThrowOutcome playOutcome = subject.throwDiceAndReturnOutcome(3,3);
		assertEquals(playOutcome.attackWins(), false);
	}
	
	@Test
	public void defenseWins(){
		Dice atacker = new  LoadedDice(1);
		Dice defense = new  LoadedDice(3);
		DiceThrowerImpl subject = new DiceThrowerImpl(atacker,defense);
		DiceThrowOutcome playOutcome = subject.throwDiceAndReturnOutcome(3,3);
		assertEquals(playOutcome.attackWins(), false);
	}
}

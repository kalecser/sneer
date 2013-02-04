package spikes.lucass.sliceWars.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import spikes.lucass.sliceWars.src.Dice;
import spikes.lucass.sliceWars.src.DiceThrower;
import spikes.lucass.sliceWars.src.LoadedDice;
import spikes.lucass.sliceWars.src.PlayOutcome;


public class DiceCompareTest {
	
	
	@Test
	public void outcomeOfDie(){
		Dice atacker = new  LoadedDice(3);
		Dice defense = new  LoadedDice(1);
		DiceThrower subject = new DiceThrower(atacker,defense);
		PlayOutcome playOutcome = subject.throwDieAndReturnOutcome(3,3);
		assertArrayEquals(new int[]{3,3,3}, playOutcome.attackDie);
		assertArrayEquals(new int[]{1,1,1}, playOutcome.defenseDie);
		assertEquals(playOutcome.attackSum, 9);
		assertEquals(playOutcome.defenseSum, 3);
	}
	
	@Test
	public void atackWins(){
		Dice atacker = new  LoadedDice(3);
		Dice defense = new  LoadedDice(1);
		DiceThrower subject = new DiceThrower(atacker,defense);
		PlayOutcome playOutcome = subject.throwDieAndReturnOutcome(3,3);
		assertEquals(playOutcome.attackWins(), true);
	}
	
	@Test
	public void onDraw_DefenseWins(){
		Dice atacker = new  LoadedDice(3);
		Dice defense = new  LoadedDice(3);
		DiceThrower subject = new DiceThrower(atacker,defense);
		PlayOutcome playOutcome = subject.throwDieAndReturnOutcome(3,3);
		assertEquals(playOutcome.attackWins(), false);
	}
	
	@Test
	public void defenseWins(){
		Dice atacker = new  LoadedDice(1);
		Dice defense = new  LoadedDice(3);
		DiceThrower subject = new DiceThrower(atacker,defense);
		PlayOutcome playOutcome = subject.throwDieAndReturnOutcome(3,3);
		assertEquals(playOutcome.attackWins(), false);
	}
}

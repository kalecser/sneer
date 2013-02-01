package spikes.lucass.sliceWars.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import spikes.lucass.sliceWars.src.Dice;
import spikes.lucass.sliceWars.src.DiceThrower;
import spikes.lucass.sliceWars.src.LoadedDice;
import spikes.lucass.sliceWars.src.PlayOutcome;


public class DiceCompareTest {
	@Test
	public void atackWins(){
		Dice atacker = new  LoadedDice(3);
		Dice defense = new  LoadedDice(1);
		List<Dice> atacking = Arrays.asList(new Dice[]{atacker,atacker,atacker});
		List<Dice> defending = Arrays.asList(new Dice[]{defense,defense,defense});
		DiceThrower subject = new DiceThrower();
		PlayOutcome playOutcome = subject.throwDieAndReturnOutcome(atacking,defending);
		assertEquals(playOutcome.attackSum, 9);
		assertEquals(playOutcome.defenseSum, 3);
		assertEquals(playOutcome.attackWins, true);
	}
}

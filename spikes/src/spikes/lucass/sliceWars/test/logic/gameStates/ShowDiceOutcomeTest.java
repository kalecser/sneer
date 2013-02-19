package spikes.lucass.sliceWars.test.logic.gameStates;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.AttackOutcome;
import spikes.lucass.sliceWars.src.logic.Cell;
import spikes.lucass.sliceWars.src.logic.DiceThrowOutcome;
import spikes.lucass.sliceWars.src.logic.gameStates.GameState;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContext.Phase;
import spikes.lucass.sliceWars.src.logic.gameStates.ShowDiceOutcome;


public class ShowDiceOutcomeTest {

	@Test
	public void testState(){
		Cell newAttackCellAfterAttack = new Cell();
		Cell newDefenseCellAfterAttack = new Cell();
		int[] attackResults = new int[]{};
		int[] defenseResults = new int[]{};
		DiceThrowOutcome diceOutcome = new DiceThrowOutcome(attackResults, defenseResults);
		AttackOutcome attackOutcome = new AttackOutcome(newAttackCellAfterAttack, newDefenseCellAfterAttack, diceOutcome);
		GameState nextGameState = new GameStateMockAdapter() {
			@Override
			public Phase getPhase() {
				return Phase.GAME_ENDED;
			}
		};
		ShowDiceOutcome showDiceOutcome = new ShowDiceOutcome(nextGameState,attackOutcome);
		assertEquals(attackOutcome, showDiceOutcome.getAttackOutcome());
		assertEquals(Phase.GAME_ENDED, showDiceOutcome.pass().getPhase());
		
	}
}

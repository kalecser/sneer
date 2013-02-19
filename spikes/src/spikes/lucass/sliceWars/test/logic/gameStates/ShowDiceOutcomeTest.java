package spikes.lucass.sliceWars.test.logic.gameStates;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.AttackOutcome;
import spikes.lucass.sliceWars.src.logic.Cell;
import spikes.lucass.sliceWars.src.logic.DiceThrowOutcome;
import spikes.lucass.sliceWars.src.logic.Player;
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
		GameState nextGameState = new GameState() {
			
			@Override
			public GameState play(int x, int y) {
				throw new RuntimeException();
			}
			
			@Override
			public GameState pass() {
				throw new RuntimeException();
			}
			
			@Override
			public Player getWhoIsPlaying() {
				throw new RuntimeException();
			}
			
			@Override
			public String getPhaseName() {
				throw new RuntimeException();
			}
			
			@Override
			public Phase getPhase() {
				throw new RuntimeException();
			}
			
			@Override
			public boolean canPass() {
				throw new RuntimeException();
			}
		};
		GameState showDiceOutcome = new ShowDiceOutcome(nextGameState,attackOutcome);
		
	}
}

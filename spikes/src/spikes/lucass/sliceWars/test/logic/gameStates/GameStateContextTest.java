package spikes.lucass.sliceWars.test.logic.gameStates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.AttackOutcome;
import spikes.lucass.sliceWars.src.logic.Cell;
import spikes.lucass.sliceWars.src.logic.DiceThrowOutcome;
import spikes.lucass.sliceWars.src.logic.gameStates.GameState;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContext;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContext.Phase;
import spikes.lucass.sliceWars.src.logic.gameStates.ShowDiceOutcome;

public class GameStateContextTest {
	
	@Test
	public void testContext(){		
		
		GameStateMockAdapter gameStateMockAdapter = new GameStateMockAdapter(){
			@Override
			public GameState play(int x, int y) {
				return new GameStateMockAdapter(){
					@Override
					public Phase getPhase() {
						return Phase.FIRST_DICE_DISTRIBUTION;
					}
				};
			}
			
			@Override
			public Phase getPhase() {
				return Phase.FILL_ALL_CELLS;
			}
		};
		GameStateContext subject = new GameStateContext(null, gameStateMockAdapter);
		assertEquals(Phase.FILL_ALL_CELLS, subject.getPhase());
		subject.play(0, 0);
		assertEquals(Phase.FIRST_DICE_DISTRIBUTION, subject.getPhase());
	}
	
	@Test
	public void testGetAttackOutcomeWhenThereIsntOne(){
		GameStateMockAdapter gameStateMockAdapter = new GameStateMockAdapter(){
			@Override
			public Phase getPhase() {
				return Phase.FILL_ALL_CELLS;
			}
		};
		GameStateContext subject = new GameStateContext(null, gameStateMockAdapter);
		assertNull(subject.getAttackOutcomeOrNull());
	}
	
	@Test
	public void testGetAttackOutcome(){
		AttackOutcome attackOutcome = new AttackOutcome(new Cell(), new Cell(),new DiceThrowOutcome(new int[]{}, new int[]{}));
		GameState showDiceOutcome = new ShowDiceOutcome(null, attackOutcome);
		GameStateContext subject = new GameStateContext(null, showDiceOutcome);
		assertEquals(attackOutcome, subject.getAttackOutcomeOrNull());
	}

}

package spikes.lucass.sliceWars.test.logic.gameStates;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContext;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContext.Phase;

public class GameStateContextTest {
	
	@Test
	public void testContext(){
		
		GameStateContext subject = new GameStateContext(0,null);
		assertEquals(Phase.FILL_ALL_CELLS, subject.getPhase());
		
	}

}

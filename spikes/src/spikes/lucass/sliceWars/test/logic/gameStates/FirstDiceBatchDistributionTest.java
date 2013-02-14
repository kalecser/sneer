package spikes.lucass.sliceWars.test.logic.gameStates;

import static org.junit.Assert.fail;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.HexagonBoardFactory;
import spikes.lucass.sliceWars.src.logic.gameStates.FirstDiceBatchDistribution;


public class FirstDiceBatchDistributionTest {
	
	@Test
	public void testState(){
		HexagonBoardFactory hexagonBoard = new HexagonBoardFactory(0, 0, 2, 2);
		FirstDiceBatchDistribution subject = new FirstDiceBatchDistribution(hexagonBoard.createBoard());
		fail();
	}

}

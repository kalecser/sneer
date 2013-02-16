package spikes.lucass.sliceWars.test.logic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.Player;

public class PlayerTest {
	
	@Test
	public void nextPlayerTest(){
		int playerCount = 3;
		Player subject = new Player(1,playerCount);
		assertEquals(1,subject.getPlayerNumber());
		assertEquals(2,subject.next().getPlayerNumber());
	}
	
	@Test
	public void playerRotationTest(){
		int playerCount = 2;
		Player subject = new Player(2,playerCount);
		assertEquals(2,subject.getPlayerNumber());
		assertEquals(1,subject.next().getPlayerNumber());
	}

}

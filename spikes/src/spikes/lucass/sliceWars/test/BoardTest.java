package spikes.lucass.sliceWars.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import spikes.lucass.sliceWars.src.Cell;
import spikes.lucass.sliceWars.src.Board;


public class BoardTest {

	@Test
	public void testHexagonBoard(){
		Board subject = new Board();
		Cell left = new Cell();
		Cell middle = new Cell();
		Cell right = new Cell();
		subject.addCell(left);
		subject.addCell(middle);
		subject.addCell(right);
		subject.link(left,middle);
		subject.link(middle,right);
		assertTrue(subject.areLinked(left,middle));
		assertTrue(subject.areLinked(middle,left));
		assertTrue(subject.areLinked(middle,right));
		assertFalse(subject.areLinked(left,right));
	}
	
}

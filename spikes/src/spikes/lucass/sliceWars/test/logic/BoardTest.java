package spikes.lucass.sliceWars.test.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Polygon;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.BoardImpl;
import spikes.lucass.sliceWars.src.logic.Player;


public class BoardTest {

	@Test
	public void addSomeCellsAndLinkThem_CheckIfOk(){
		BoardImpl subject = new BoardImpl();
		
		Polygon square1 = getSquare1();
		Polygon square2 = getSquare2();
		Polygon square3 = getSquare3();
		
		subject.createAndAddToBoardCellForPolygon(square1);
		subject.createAndAddToBoardCellForPolygon(square2);
		subject.createAndAddToBoardCellForPolygon(square3);
		
		subject.createAndAddToBoardCellForPolygon(square1);
		subject.createAndAddToBoardCellForPolygon(square2);
		subject.createAndAddToBoardCellForPolygon(square3);
		subject.link(square1,square2);
		subject.link(square2,square3);
		assertTrue(subject.areLinked(square1,square2));
		assertTrue(subject.areLinked(square2,square1));
		assertTrue(subject.areLinked(square2,square3));
		assertFalse(subject.areLinked(square1,square3));
	}
	
	@Test
	public void addSomeCellsAndSetOwners_ShouldSayBoardIsFull(){
		BoardImpl subject = new BoardImpl();
		
		Polygon irrelevant = new Polygon();
		
		subject.createAndAddToBoardCellForPolygon(irrelevant).setOwner(Player.PLAYER1);
		subject.createAndAddToBoardCellForPolygon(irrelevant).setOwner(Player.PLAYER1);
		subject.createAndAddToBoardCellForPolygon(irrelevant).setOwner(Player.PLAYER1);
		assertTrue(subject.isFilled());
	}
	
	@Test
	public void addSomeCellsAndSetOwners_CheckIfLinkedCellCountIsRight(){
		BoardImpl subject = new BoardImpl();
		
		Polygon irrelevant = new Polygon();
		
		BoardCell cellLeftTop = subject.createAndAddToBoardCellForPolygon(irrelevant);
		BoardCell cellMiddleTop = subject.createAndAddToBoardCellForPolygon(irrelevant);
		BoardCell cellRightTop = subject.createAndAddToBoardCellForPolygon(irrelevant);
		BoardCell cellLeftCenter = subject.createAndAddToBoardCellForPolygon(irrelevant);
		BoardCell cellMiddleCenter = subject.createAndAddToBoardCellForPolygon(irrelevant);
		BoardCell cellRightCenter = subject.createAndAddToBoardCellForPolygon(irrelevant);
		BoardCell cellLeftBottom = subject.createAndAddToBoardCellForPolygon(irrelevant);
		BoardCell cellMiddleBottom = subject.createAndAddToBoardCellForPolygon(irrelevant);
		BoardCell cellRightBottom = subject.createAndAddToBoardCellForPolygon(irrelevant);
		
		subject.link(cellLeftTop, cellMiddleTop);
		subject.link(cellLeftTop, cellMiddleCenter);
		subject.link(cellLeftTop, cellLeftCenter);
		
		subject.link(cellMiddleTop,cellRightTop);
		subject.link(cellMiddleTop,cellRightCenter);
		subject.link(cellMiddleTop,cellMiddleCenter);
		subject.link(cellMiddleTop,cellLeftCenter);
		
		subject.link(cellRightTop,cellRightCenter);
		subject.link(cellRightTop,cellMiddleCenter);
		
		subject.link(cellLeftCenter, cellMiddleCenter);
		subject.link(cellLeftCenter, cellMiddleBottom);
		subject.link(cellLeftCenter, cellLeftBottom);
		
		subject.link(cellMiddleCenter,cellRightCenter);
		subject.link(cellMiddleCenter,cellRightBottom);
		subject.link(cellMiddleCenter,cellMiddleBottom);
		subject.link(cellMiddleCenter,cellLeftBottom);
		
		subject.link(cellRightCenter,cellRightBottom);
		subject.link(cellRightCenter,cellMiddleBottom);
		
		subject.link(cellLeftBottom,cellMiddleBottom);
		
		subject.link(cellMiddleBottom, cellRightBottom);
		
		cellLeftTop.setOwner(Player.PLAYER1);
		cellMiddleTop.setOwner(Player.PLAYER2);
		cellRightTop.setOwner(Player.PLAYER2);
		
		cellLeftCenter.setOwner(Player.PLAYER2);
		cellMiddleCenter.setOwner(Player.PLAYER2);
		cellRightCenter.setOwner(Player.PLAYER1);
		
		cellLeftBottom.setOwner(Player.PLAYER1);
		cellMiddleBottom.setOwner(Player.PLAYER1);
		cellRightBottom.setOwner(Player.PLAYER1);
		
		int linkedCount = subject.getBiggestLinkedCellCountForPlayer(Player.PLAYER1);
		assertEquals(4, linkedCount);
	}

	private Polygon getSquare1() {
		int[] squareXPoints = new int[]{ 0,10,10, 0};
		int[] squareYPoints = new int[]{ 0, 0,10,10};
		int squareNPoints = 4;
		Polygon square = new Polygon(squareXPoints, squareYPoints, squareNPoints);
		return square;
	}
	
	private Polygon getSquare2() {
		int[] squareXPoints = new int[]{10,20,20,10};
		int[] squareYPoints = new int[]{ 0, 0,10,10};
		int squareNPoints = 4;
		Polygon square = new Polygon(squareXPoints, squareYPoints, squareNPoints);
		return square;
	}
	
	private Polygon getSquare3() {
		int[] squareXPoints = new int[]{20,30,30,20};
		int[] squareYPoints = new int[]{ 0, 0,10,10};
		int squareNPoints = 4;
		Polygon square = new Polygon(squareXPoints, squareYPoints, squareNPoints);
		return square;
	}
}

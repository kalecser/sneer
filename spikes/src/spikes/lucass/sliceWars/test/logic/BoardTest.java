package spikes.lucass.sliceWars.test.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Polygon;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.BoardImpl;
import spikes.lucass.sliceWars.src.logic.Cell;
import spikes.lucass.sliceWars.src.logic.Player;


public class BoardTest {

	@Test
	public void addSomeCellsAndLinkThem_CheckIfOk(){
		BoardImpl subject = new BoardImpl();
		
		Polygon irrelevant = new Polygon();
		
		BoardCell cell1 = subject.createAndAddToBoardCellForPolygon(irrelevant);
		BoardCell cell2 = subject.createAndAddToBoardCellForPolygon(irrelevant);
		BoardCell cell3 = subject.createAndAddToBoardCellForPolygon(irrelevant);
		
		subject.link(cell1,cell2);
		subject.link(cell2,cell3);
		assertTrue(subject.areLinked(cell1,cell2));
		assertTrue(subject.areLinked(cell2,cell1));
		assertTrue(subject.areLinked(cell2,cell3));
		assertFalse(subject.areLinked(cell1,cell3));
	}
	
	@Test
	public void addSomeCells_ThenRemoveOne(){
		BoardImpl subject = new BoardImpl();
		
		Polygon irrelevant = new Polygon();
		
		BoardCell cell1 = subject.createAndAddToBoardCellForPolygon(irrelevant);
		BoardCell cell2 = subject.createAndAddToBoardCellForPolygon(irrelevant);
		subject.link(cell1,cell2);
		
		subject.remove(cell1);
		assertTrue(!subject.getBoardCells().contains(cell1));
		assertFalse(subject.areLinked(cell1,cell2));
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
	public void addSomeCellsAndSetOwnersAndFillThem_ShouldSayPlayerCantPlay(){
		BoardImpl subject = new BoardImpl();
		
		Polygon irrelevant = new Polygon();
		
		BoardCell cell1 = subject.createAndAddToBoardCellForPolygon(irrelevant);
		cell1.setOwner(Player.PLAYER1);
		BoardCell cell2 = subject.createAndAddToBoardCellForPolygon(irrelevant);
		cell2.setOwner(Player.PLAYER1);
		cell1.setDiceCount(1);
		cell2.setDiceCount(1);
		assertTrue(!subject.areaAllCellsFilled(Player.PLAYER1));
		cell1.setDiceCount(Cell.MAX_DICE);
		cell2.setDiceCount(Cell.MAX_DICE);
		assertTrue(subject.areaAllCellsFilled(Player.PLAYER1));
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
	
	@Test
	public void askIfRemovingCellWillLeaveOrphanCells(){
		BoardImpl subject = new BoardImpl();
		
		Polygon irrelevant = new Polygon();
		
		BoardCell cellLeft = subject.createAndAddToBoardCellForPolygon(irrelevant);
		BoardCell cellMiddle = subject.createAndAddToBoardCellForPolygon(irrelevant);
		BoardCell cellRight = subject.createAndAddToBoardCellForPolygon(irrelevant);
		
		cellLeft.setDiceCount(1);
		cellMiddle.setDiceCount(2);
		cellRight.setDiceCount(3);
		
		subject.link(cellLeft,cellMiddle);
		subject.link(cellMiddle,cellRight);

		assertTrue(subject.removingCellWillLeaveOrphans(cellMiddle));
		assertFalse(subject.removingCellWillLeaveOrphans(cellLeft));
	}
}

package spikes.lucass.sliceWars.src.logic;

import java.awt.Polygon;


public class BoardCell {
	
	public Polygon polygon;
	public Cell cell;
	
	public BoardCell(Polygon p) {
		polygon = p;
		cell = new Cell();
	}

}

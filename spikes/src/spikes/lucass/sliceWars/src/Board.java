package spikes.lucass.sliceWars.src;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Board {

	Map<Cell, List<Cell>> cellWithLinkedCells = new LinkedHashMap<Cell, List<Cell>>();
	
	public void addCell(Cell cell) {
		cellWithLinkedCells.put(cell, new ArrayList<Cell>());
	}

	public void link(Cell cell1, Cell cell2) {
		List<Cell> cell1LinkedCells = cellWithLinkedCells.get(cell1);
		cell1LinkedCells.add(cell2);
		List<Cell> cell2LinkedCells = cellWithLinkedCells.get(cell2);
		cell2LinkedCells.add(cell1);
	}

	public boolean areLinked(Cell cell1, Cell cell2) {
		List<Cell> cell1LinkedCells = cellWithLinkedCells.get(cell1);
		return cell1LinkedCells.contains(cell2);
	}

}

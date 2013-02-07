package spikes.lucass.sliceWars.src;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Board {

	Map<Polygon, List<Polygon>> linkedPolygons = new LinkedHashMap<Polygon, List<Polygon>>();
	
	public void addCell(Polygon polygon) {
		linkedPolygons.put(polygon, new ArrayList<Polygon>());
	}

	public void link(Polygon polygon1, Polygon polygon2) {
		List<Polygon> polygon1LinkedCells = linkedPolygons.get(polygon1);
		polygon1LinkedCells.add(polygon2);
		List<Polygon> polygon2LinkedCells = linkedPolygons.get(polygon2);
		polygon2LinkedCells.add(polygon1);
	}

	public boolean areLinked(Polygon polygon1, Polygon polygon2) {
		List<Polygon> cell1LinkedCells = linkedPolygons.get(polygon1);
		return cell1LinkedCells.contains(polygon2);
	}

}

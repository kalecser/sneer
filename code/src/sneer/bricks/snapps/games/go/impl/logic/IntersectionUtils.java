package sneer.bricks.snapps.games.go.impl.logic;

import static sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor.BLACK;
import static sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor.WHITE;


public class IntersectionUtils {

	public static Intersection[][] createIntersections(int size) {
		Intersection[][] intersections = new Intersection[size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				Intersection newOne = new Intersection();
				intersections[x][y] = newOne;
				if (x != 0) newOne.connectToYourLeft(intersections[x - 1][y]);
				if (y != 0) newOne.connectUp(intersections[x][y - 1]);
			}
		}
		return intersections;
	}

	public static boolean sameSituation(Intersection[][] intersectionsA, Intersection[][] intersectionsB) {
		if(intersectionsA == null || intersectionsB == null){
			return intersectionsA == intersectionsB;
		}
		for (int x = 0; x < intersectionsA.length; x++) {
			for (int y = 0; y < intersectionsA[x].length; y++) {
				if(intersectionsA[x][y]._stone != intersectionsB[x][y]._stone){
					return false;
				}
			}
		}
		return true;
	}

	public static Intersection[][] copy(Intersection[][] intersections) {
		if(intersections.length == 0)
			return new Intersection[0][0];
		Intersection[][] copy =  new Intersection[intersections.length][intersections[0].length];
		
		for (int x = 0; x < intersections.length; x++) {
			for (int y = 0; y < intersections[x].length; y++) {
				copy[x][y] = intersections[x][y].copy();
			}
		}
		
		return copy;
	}

	public static String print(final Intersection[][] intersections) {
		StringBuffer result= new StringBuffer();
		for (int y = 0; y < intersections.length; y++) {
			for (int x = 0; x < intersections[y].length; x++) {
				GoBoard.StoneColor stone = intersections[x][y]._stone;
				if(stone == WHITE)
					result.append(" w");
				else if(stone == BLACK)
					result.append(" b");
				else
					result.append(" +");
			}
			result.append("\n");
		}
		return result.toString();
	}

}

package sneer.bricks.snapps.games.go.impl.logic;

import static sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor.BLACK;
import static sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor.WHITE;


public class IntersectionUtils {

	private static final char BLACK_CHAR = 'b';
	private static final char WHITE_CHAR = 'w';

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

	public static String print(final Intersection[][] intersections) {
		StringBuffer result= new StringBuffer();
		for (int y = 0; y < intersections.length; y++) {
			for (int x = 0; x < intersections[y].length; x++) {
				GoBoard.StoneColor stone = intersections[x][y]._stone;
				if(stone == WHITE)
					result.append(" "+WHITE_CHAR);
				else if(stone == BLACK)
					result.append(" "+BLACK_CHAR);
				else
					result.append(" +");
			}
			result.append("\n");
		}
		return result.toString();
	}

	public static void setupLine(Intersection[][] intersection,int y, String line) {
		int x = 0;
		for(char symbol : line.toCharArray()) {
			if (symbol == ' ') continue;
			
			GoBoard.StoneColor stone = null;
			if(symbol == WHITE_CHAR) stone = WHITE;
			if(symbol == BLACK_CHAR) stone = BLACK;
			
			intersection[x][y]._stone = stone;
			x++;
		}
	}

	public static void setup(Intersection[][] intersection,String[] setup){
		for (int y = 0; y < setup.length; y++)
			setupLine(intersection,y, setup[y]);
	}

}

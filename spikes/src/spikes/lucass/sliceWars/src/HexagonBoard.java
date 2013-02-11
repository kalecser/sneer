package spikes.lucass.sliceWars.src;

import java.awt.Polygon;


public class HexagonBoard {

	static Board createBoard() {
		Board board = new Board();
		
		int width = 5;
		int height = 10;
		Polygon[][] poligons = new Polygon[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				poligons[x][y] = createHexagonOnPosition(x,y);
			}
		}
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				board.createAndAddToBoardCellForPolygon(poligons[x][y]);
			}
		}
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if(y-2 >=0)
					board.link(poligons[x][y], poligons[x][y-2]);
				
				if(y%2==0){
					if(x-1 >= 0 && y-1 >=0)
						board.link(poligons[x][y], poligons[x-1][y-1]);
					if( y-1 >=0)
						board.link(poligons[x][y], poligons[x][y-1]);
				}else{
					if( y-1 >=0)
						board.link(poligons[x][y], poligons[x][y-1]);
					if(x+1 < poligons.length && y-1 >=0)
						board.link(poligons[x][y], poligons[x+1][y-1]);	
				}
			}
		}
		
		return board;
	}

	public static Polygon createHexagonOnPosition(int x, int y){
		int size = 100;
		int xOffset = (y%2==0)?0:(size/4)*3;
		int xMoved = (x*size) + (x*(size/2)) + xOffset;
		int yMoved = (y*size) - (y*(size/2));
		return drawHexagon(xMoved,yMoved,size);
	}

	static Polygon drawHexagon(int x, int y, int sideLenght) {
		int quarter = sideLenght/4;
		int half = sideLenght/2;
		
		int[] squareXPoints = new int[]{x     ,x+quarter,x+(quarter*3),x+sideLenght,x+(quarter*3),x+quarter   };
		int[] squareYPoints = new int[]{y+half,y        ,y            ,y+half      ,y+sideLenght ,y+sideLenght};
		int squareNPoints = 6;
		Polygon square = new Polygon(squareXPoints, squareYPoints, squareNPoints);
		return square;
	}

}

package spikes.lucass.sliceWars.src.logic;

import java.awt.Polygon;


public class HexagonBoard {

	private int _xStart;
	private int _yStart;
	private int _width;
	private int _height;

	public HexagonBoard(int xStart,int yStart,int width,int height) {
		_xStart = xStart;
		_yStart = yStart;
		_width = width;
		_height = height;
	}
	
	public Board createBoard() {
		Board board = new Board();
		
		Polygon[][] poligons = new Polygon[_width][_height];
		for (int x = 0; x < _width; x++) {
			for (int y = 0; y < _height; y++) {
				poligons[x][y] = createHexagonOnPosition(x,y);
			}
		}
		
		for (int x = 0; x < _width; x++) {
			for (int y = 0; y < _height; y++) {
				board.createAndAddToBoardCellForPolygon(poligons[x][y]);
			}
		}
		
		for (int x = 0; x < _width; x++) {
			for (int y = 0; y < _height; y++) {
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

	public Polygon createHexagonOnPosition(int x, int y){
		int size = 100;
		int xOffset = (y%2==0)?0:(size/4)*3;
		int xMoved = (x*size) + (x*(size/2)) + xOffset;
		int yMoved = (y*size) - (y*(size/2));
		return drawHexagon(_xStart+xMoved,_yStart+ yMoved,size);
	}

	private Polygon drawHexagon(int x, int y, int sideLenght) {
		int quarter = sideLenght/4;
		int half = sideLenght/2;
		
		int[] squareXPoints = new int[]{x     ,x+quarter,x+(quarter*3),x+sideLenght,x+(quarter*3),x+quarter   };
		int[] squareYPoints = new int[]{y+half,y        ,y            ,y+half      ,y+sideLenght ,y+sideLenght};
		int squareNPoints = 6;
		Polygon square = new Polygon(squareXPoints, squareYPoints, squareNPoints);
		return square;
	}

}

package spikes.lucass.sliceWars.src.logic;

import java.awt.Polygon;
import java.util.Random;

public class HexagonBoardFactory {

	private int _x;
	private int _y;
	private int _columns;
	private int _lines;
	private BoardCell[][] _boardCells;
	private int _randomlyRemoveCount;
	private Random _random;
	private CellAttack _cellAttack;

	public HexagonBoardFactory(int x,int y,int lines,int columns,int randomlyRemoveCount, final Random random) {
		_random = random;
		_cellAttack = new CellAttack(new DiceThrowerImpl(new DiceImpl(random), new DiceImpl(random)));
		if(randomlyRemoveCount >= lines * columns){
			throw new RuntimeException("Invalid randomlyRemoveCount "+ randomlyRemoveCount);
		}
		_x = x;
		_y = y;
		_columns = columns;
		_lines = lines;
		_randomlyRemoveCount = randomlyRemoveCount;
	}
	
	public Board createBoard() {
		_boardCells = new BoardCell[_columns][_lines];
		Board board = internalCreateBoard();
		int cellsToRemove = _randomlyRemoveCount;
		return removeCellsRandomly(board, cellsToRemove);
	}

	public Board removeCellsRandomly(Board board, int cellsToRemove) {
		for (int i = 0; i < cellsToRemove; i++) {
			int col = _random.nextInt(_columns);
			int line = _random.nextInt(_lines);
			while(_boardCells[col][line] == null || board.removingCellWillLeaveOrphans(_boardCells[col][line] )){
				col = _random.nextInt(_columns);
				line = _random.nextInt(_lines);
			}
			board.remove(_boardCells[col][line]);
			_boardCells[col][line] = null;
		}
		return board;
	}

	private Board internalCreateBoard() {
		BoardImpl board = new BoardImpl();
		
		Polygon[][] poligons = new Polygon[_columns][_lines];
		for (int x = 0; x < _columns; x++) {
			for (int y = 0; y < _lines; y++) {
				poligons[x][y] = createHexagonOnPosition(x,y);
			}
		}
		
		for (int x = 0; x < _columns; x++) {
			for (int y = 0; y < _lines; y++) {
				_boardCells[x][y] = board.createAndAddToBoardCellForPolygon(poligons[x][y],_cellAttack);
			}
		}
		
		for (int x = 0; x < _columns; x++) {
			for (int y = 0; y < _lines; y++) {
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
		return drawHexagon(_x+xMoved,_y+ yMoved,size);
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

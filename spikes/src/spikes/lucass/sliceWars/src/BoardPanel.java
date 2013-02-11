package spikes.lucass.sliceWars.src;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class BoardPanel extends JPanel {

	private Board _board;
	private List<Polygon> polygonsClicked = new ArrayList<Polygon>();
	
	public BoardPanel(Board board) {
		_board = board;
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Polygon polygonAt = _board.getPolygonAt(e.getX(),e.getY());
				if(polygonAt != null)
					polygonsClicked.add(polygonAt);
				Set<BoardCell> polygonsLinked = _board.getLinked(polygonAt);
				for (BoardCell boardCell : polygonsLinked) {
					polygonsClicked.add(boardCell.polygon);
				}
				System.out.println(polygonAt);
			}
		});
		
		new Thread(){
			@Override
			public void run() {
				while(true){
					repaint();
					try {
						sleep(100);
					} catch (InterruptedException e) {
					}
				}
			};
		}.start();
	}

	@Override
	protected void paintComponent(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.WHITE);
		g2.fill(g2.getClipBounds());
		render(g2);
		paintComponents(g2);
		g.dispose();
	}

	private void render(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.setColor(Color.BLACK);
		Set<BoardCell> boardCells = _board.getBoardCells();
		for (BoardCell boardCell : boardCells) {
			g2.draw(boardCell.polygon);
			if(polygonsClicked.contains(boardCell.polygon))
				g2.fill(boardCell.polygon);
			g2.drawString("d:"+boardCell.cell.diceCount, boardCell.polygon.xpoints[0], boardCell.polygon.ypoints[0]);
		}
	}
	
	//-------------------------------------------------------
	
	public static void main(String[] args) {
		
		Board board = createBoard();
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.add(new BoardPanel(board));
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
	
	private static Board createBoard() {
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
	
	private static Polygon drawHexagon(int x, int y, int sideLenght) {
		int quarter = sideLenght/4;
		int half = sideLenght/2;
		
		int[] squareXPoints = new int[]{x     ,x+quarter,x+(quarter*3),x+sideLenght,x+(quarter*3),x+quarter   };
		int[] squareYPoints = new int[]{y+half,y        ,y            ,y+half      ,y+sideLenght ,y+sideLenght};
		int squareNPoints = 6;
		Polygon square = new Polygon(squareXPoints, squareYPoints, squareNPoints);
		return square;
	}
}

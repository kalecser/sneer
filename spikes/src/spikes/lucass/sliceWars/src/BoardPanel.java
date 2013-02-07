package spikes.lucass.sliceWars.src;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class BoardPanel extends JPanel {

	private Board _board;

	public BoardPanel(Board board) {
		_board = board;
	}

	@Override
	protected void paintComponent(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g;
		render(g2);
		paintComponents(g2);
		g.dispose();
	}

	private void render(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		Set<BoardCell> boardCells = _board.getBoardCells();
		for (BoardCell boardCell : boardCells) {
			g2.draw(boardCell.polygon);
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
		Polygon square1 = getSquare1();
		board.addCell(square1);
		
		Polygon square2 = getSquare2();
		board.addCell(square2);
		
		Polygon square3 = getSquare3();
		board.addCell(square3);
		
		board.addCell(square1);
		board.addCell(square2);
		board.addCell(square3);
		board.link(square1,square2);
		board.link(square2,square3);
		return board;
	}
	
	private static Polygon getSquare1() {
		int[] squareXPoints = new int[]{ 0,10,10, 0};
		int[] squareYPoints = new int[]{ 0, 0,10,10};
		int squareNPoints = 4;
		Polygon square = new Polygon(squareXPoints, squareYPoints, squareNPoints);
		return square;
	}
	
	private static Polygon getSquare2() {
		int[] squareXPoints = new int[]{10,20,20,10};
		int[] squareYPoints = new int[]{ 0, 0,10,10};
		int squareNPoints = 4;
		Polygon square = new Polygon(squareXPoints, squareYPoints, squareNPoints);
		return square;
	}
	
	private static Polygon getSquare3() {
		int[] squareXPoints = new int[]{20,30,30,20};
		int[] squareYPoints = new int[]{ 0, 0,10,10};
		int squareNPoints = 4;
		Polygon square = new Polygon(squareXPoints, squareYPoints, squareNPoints);
		return square;
	}
}

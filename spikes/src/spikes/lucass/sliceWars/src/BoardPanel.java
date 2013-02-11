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
		
		Board board = HexagonBoard.createBoard();
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.add(new BoardPanel(board));
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}

package spikes.lucass.sliceWars.src;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import spikes.lucass.sliceWars.src.gameStates.FillAllCellPhase;
import spikes.lucass.sliceWars.src.gameStates.GameState;

public class BoardPanel extends JPanel {

	private Board _board;
	private GameState _phase;
	
	public BoardPanel(Board board) {
		_board = board;
		_phase = new FillAllCellPhase(board);		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				_phase = _phase.play(e.getX(), e.getY());
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
			g2.drawString("d:"+boardCell.cell.diceCount+" owner: "+boardCell.cell.owner, boardCell.polygon.xpoints[0], boardCell.polygon.ypoints[0]);
		}
	}
	
	//-------------------------------------------------------
	
	public static void main(String[] args) {
		
//		Board board = HexagonBoard.createBoard(5,10);
		Board board = HexagonBoard.createBoard(2,2);
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.add(new BoardPanel(board));
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}

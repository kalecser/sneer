package spikes.lucass.sliceWars.src.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.HexagonBoardFactory;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.FillAllCellPhase;
import spikes.lucass.sliceWars.src.logic.gameStates.GameState;

public class BoardPanel extends JPanel {

	private Board _board;
	private GameState _phase;
	private static JLabel phaseLabel;
	private static JButton pass;
	private final static AtomicBoolean _gameRunning = new AtomicBoolean();
	private final static Color[] colors = new Color[]{Color.GRAY, Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW};
	
	
	public BoardPanel(Board board) {
		_board = board;
		int player = 1;
		_phase = new FillAllCellPhase(new Player(player, 3), board);		
		phaseLabel.setText(_phase.getPhaseName() + " Turn: player "+_phase.getWhoIsPlaying().getPlayerNumber());
		addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent e) {
				_phase = _phase.play(e.getX(), e.getY());
				phaseLabel.setText(_phase.getPhaseName() + " Turn: player "+_phase.getWhoIsPlaying().getPlayerNumber());
				pass.setEnabled(_phase.canPass());
		}});
		pass = new JButton("Pass");
		pass.setEnabled(false);
		pass.addActionListener(new ActionListener(){@Override public void actionPerformed(ActionEvent e) {
			_phase = _phase.pass();
			phaseLabel.setText(_phase.getPhaseName() + " Turn: player "+_phase.getWhoIsPlaying().getPlayerNumber());
			pass.setEnabled(_phase.canPass());
		}});
		

		new Thread(){
		@Override public void run() {
			_gameRunning.set(true);
			while(_gameRunning.get()){
				repaint();
				try {
					sleep(100);
				} catch (InterruptedException e) {}
			}
		}}.start();
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
			int playerNumber = boardCell.getOwner().getPlayerNumber();
			g2.setColor(colors[playerNumber]);
			g2.fill(boardCell.getPolygon());
			g2.setColor(Color.BLACK);
			g2.draw(boardCell.getPolygon());
			g2.setColor(Color.WHITE);
			g2.drawString(playerNumber+" dados:"+boardCell.getDiceCount(), boardCell.getPolygon().xpoints[0] + 10, boardCell.getPolygon().ypoints[0]);
		}
	}
	
	//-------------------------------------------------------
	
	public static void main(String[] args) {
		int x = 10;
		int y = 10;
		int lines = 6;
		int columns = 6;
		int randomlyRemoveCount = 9;
		HexagonBoardFactory hexagonBoard = new HexagonBoardFactory(x, y, lines, columns, randomlyRemoveCount);
		Board board = hexagonBoard.createBoard();
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				_gameRunning.set(false);
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		
		frame.setLayout(new BorderLayout());
		phaseLabel = new JLabel();
		phaseLabel.setText("Fill all cells");
		frame.add(phaseLabel, BorderLayout.NORTH);
		BoardPanel comp = new BoardPanel(board);
		frame.add(comp, BorderLayout.CENTER);
		
		frame.add(pass, BorderLayout.SOUTH);
		frame.setSize(800, 600);
		frame.pack();
		frame.setVisible(true);
	}
}

package spikes.lucass.sliceWars.src.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import spikes.lucass.sliceWars.src.gui.drawers.PassButtonDrawer;
import spikes.lucass.sliceWars.src.gui.drawers.PhaseDescriptionDrawer;
import spikes.lucass.sliceWars.src.gui.drawers.SimpleCellDrawer;
import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.HexagonBoardFactory;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.FillAllCellPhase;
import spikes.lucass.sliceWars.src.logic.gameStates.GameState;

public class GamePanel extends JPanel {

	private Board _board;
	private GameState _phase;
	
	private SimpleCellDrawer simpleCellDrawer = new SimpleCellDrawer();
	private PhaseDescriptionDrawer phaseLabel = new PhaseDescriptionDrawer(10,25);
	private PassButtonDrawer passButtonDrawer = new PassButtonDrawer(500,18);
	
	private AtomicBoolean _gameRunning = new AtomicBoolean(true);
	private BufferedImage _background;
	
	public GamePanel() {
		int x = 10;
		int y = 50;
		int lines = 3;
		int columns = 3;
		int randomlyRemoveCount = 1;
		HexagonBoardFactory hexagonBoard = new HexagonBoardFactory(x, y, lines, columns, randomlyRemoveCount);
		Board board = hexagonBoard.createBoard();
		
		InputStream resourceAsStream = GamePanel.class.getResourceAsStream("pw_maze_white.png");
		try {
			_background = ImageIO.read(resourceAsStream);
		} catch (IOException e1) {
			throw new RuntimeException("Background not found");
		}
		
		_board = board;
		int player = 1;
		_phase = new FillAllCellPhase(new Player(player, 3), board);	
		phaseLabel.setPhase(_phase);
		
		addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent e) {
			_phase = _phase.play(e.getX(), e.getY());
			phaseLabel.setPhase(_phase);
			passButtonDrawer.click(e.getX(), e.getY());
			passButtonDrawer.setVisible(_phase.canPass());
		}});
		passButtonDrawer.setVisible(false);
		passButtonDrawer.addClickListener(new Runnable() {@Override	public void run() {
			_phase = _phase.pass();
			phaseLabel.setPhase(_phase);
			passButtonDrawer.setVisible(_phase.canPass());
		}});
		

		new Thread(){@Override public void run() {
			_gameRunning.set(true);
			while(_gameRunning.get()){
				repaint();
				try {
					sleep(100);
				} catch (InterruptedException e) {}
			}
		}}.start();
	}

	public void stopGameThread(){
		_gameRunning.set(false);
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
		drawBackground(g2);
		drawCells(g2);
		phaseLabel.draw(g2);
		passButtonDrawer.draw(g2);
	}

	private void drawBackground(Graphics2D g2) {
		for (int x=0; x<= getWidth()/_background.getWidth(); x++){
			for (int y=0; y<= getHeight()/_background.getHeight(); y++){
				g2.drawImage(_background, x * _background.getWidth(), y * _background.getHeight(), this);
	        }
	    }
	}

	private void drawCells(Graphics2D g2) {
		Collection<BoardCell> boardCells = _board.getBoardCells();
		for (BoardCell boardCell : boardCells) {
			simpleCellDrawer.draw(boardCell,g2);
		}
	}
}

package spikes.lucass.sliceWars.src.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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
	
	public GamePanel() {
		int x = 10;
		int y = 50;
		int lines = 6;
		int columns = 6;
		int randomlyRemoveCount = 9;
		HexagonBoardFactory hexagonBoard = new HexagonBoardFactory(x, y, lines, columns, randomlyRemoveCount);
		Board board = hexagonBoard.createBoard();
		
		
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
		drawCells(g2);
		phaseLabel.draw(g2);
		passButtonDrawer.draw(g2);
	}

	private void drawCells(Graphics2D g2) {
		Set<BoardCell> boardCells = _board.getBoardCells();
		for (BoardCell boardCell : boardCells) {
			simpleCellDrawer.draw(boardCell,g2);
		}
	}
}

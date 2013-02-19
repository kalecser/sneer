package spikes.lucass.sliceWars.src.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JPanel;

import spikes.lucass.sliceWars.src.gui.drawers.BackgroundDrawer;
import spikes.lucass.sliceWars.src.gui.drawers.CellsDrawer;
import spikes.lucass.sliceWars.src.gui.drawers.Drawer;
import spikes.lucass.sliceWars.src.gui.drawers.PassButtonDrawer;
import spikes.lucass.sliceWars.src.gui.drawers.PhaseDescriptionDrawer;
import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.HexagonBoardFactory;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContext;

public class GamePanel extends JPanel {

	private GameStateContext _gameContext;
	private List<Drawer> drawers = new ArrayList<Drawer>();
	
	private AtomicBoolean _gameRunning = new AtomicBoolean(true);
	
	public GamePanel() {
		int x = 10;
		int y = 50;
		int lines = 3;
		int columns = 3;
		int randomlyRemoveCount = 1;
		HexagonBoardFactory hexagonBoard = new HexagonBoardFactory(x, y, lines, columns, randomlyRemoveCount);
		Board board = hexagonBoard.createBoard();
		
		int numberOfPlayers = 3;
		_gameContext = new GameStateContext(numberOfPlayers,board);
		
		createAndWireDrawers();
		

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

	private void createAndWireDrawers() {
		drawers.add(new PhaseDescriptionDrawer(10,25, _gameContext));
		final PassButtonDrawer passButtonDrawer = new PassButtonDrawer(500,18);
		drawers.add(passButtonDrawer);
		drawers.add(new CellsDrawer(_gameContext));
		drawers.add(new BackgroundDrawer());
		
		addMouseListener(new MouseAdapter(){@Override public void mouseClicked(MouseEvent e) {
			_gameContext.play(e.getX(), e.getY());
			passButtonDrawer.click(e.getX(), e.getY());
			passButtonDrawer.setVisible(_gameContext.canPass());
		}});
		passButtonDrawer.setVisible(false);
		passButtonDrawer.addClickListener(new Runnable() {@Override	public void run() {
			_gameContext.pass();
			passButtonDrawer.setVisible(_gameContext.canPass());
		}});
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
		
		for (Drawer drawer : drawers) drawer.draw(g2);
	}

	
}

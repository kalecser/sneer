package sneer.bricks.snapps.games.sliceWars.impl.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JPanel;

import sneer.bricks.snapps.games.sliceWars.impl.gui.drawers.AttackOutcomeDrawer;
import sneer.bricks.snapps.games.sliceWars.impl.gui.drawers.BackgroundDrawer;
import sneer.bricks.snapps.games.sliceWars.impl.gui.drawers.CellDrawer;
import sneer.bricks.snapps.games.sliceWars.impl.gui.drawers.CellsDrawer;
import sneer.bricks.snapps.games.sliceWars.impl.gui.drawers.DiceLeftDrawer;
import sneer.bricks.snapps.games.sliceWars.impl.gui.drawers.Drawer;
import sneer.bricks.snapps.games.sliceWars.impl.gui.drawers.PassButtonDrawer;
import sneer.bricks.snapps.games.sliceWars.impl.gui.drawers.PhaseDescriptionDrawer;
import sneer.bricks.snapps.games.sliceWars.impl.logic.Board;
import sneer.bricks.snapps.games.sliceWars.impl.logic.HexagonBoardFactory;
import sneer.bricks.snapps.games.sliceWars.impl.logic.Player;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.GameStateContext;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.GameStateContextImpl;

public class GamePanel extends JPanel {

	private List<Drawer> drawers = new ArrayList<Drawer>();
	
	private AtomicBoolean _gameRunning = new AtomicBoolean(true);

	private PassButtonDrawer _passButtonDrawer;

	private GameStateContext _gameContext;
	
	public GamePanel(final int numberOfPlayers,final int lines,final int columns,final int randomlyRemoveCellCount, final Random random) {
		int x = 10;
		int y = 50;		
		
		HexagonBoardFactory hexagonBoard = new HexagonBoardFactory(x, y, lines, columns, randomlyRemoveCellCount, random);
		Board board = hexagonBoard.createBoard();
		
		int remainder = ((lines*columns)-randomlyRemoveCellCount) % numberOfPlayers;
		hexagonBoard.removeCellsRandomly(board, remainder);
		
		_gameContext = new GameStateContextImpl(numberOfPlayers,board);
		
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
		drawers.add(new BackgroundDrawer());
		CellDrawer cellDrawer = new CellDrawer();
		_gameContext.setSelectedCellCallback(cellDrawer);
		_gameContext.addPlayListener(cellDrawer);
		drawers.add(new CellsDrawer(_gameContext,cellDrawer));
		drawers.add(new PhaseDescriptionDrawer(10,25, _gameContext));
		AttackOutcomeDrawer attackOutcomeDrawer = new AttackOutcomeDrawer(10,500);
		drawers.add(attackOutcomeDrawer);
		_gameContext.setAttackCallback(attackOutcomeDrawer);
		_gameContext.addPlayListener(attackOutcomeDrawer);
		DiceLeftDrawer diceLeftDrawer = new DiceLeftDrawer(300, 500);
		_gameContext.setDiceLeftCallback(diceLeftDrawer);
		drawers.add(diceLeftDrawer);
		_passButtonDrawer = new PassButtonDrawer(500,18);
		drawers.add(_passButtonDrawer);
		_passButtonDrawer.setVisible(false);
		_passButtonDrawer.addClickListener(new Runnable() {@Override	public void run() {
			_gameContext.pass();
			_passButtonDrawer.setVisible(_gameContext.canPass());
		}});
	}
	
	public void play(final int x, final int y){		
		_gameContext.play(x, y);
		_passButtonDrawer.click(x, y);
		_passButtonDrawer.setVisible(_gameContext.canPass());
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
		
		for (Drawer drawer : drawers){
			drawer.draw(g2);
		}
	}

	public Player currentPlayer() {
		return _gameContext.getWhoIsPlaying();
	}

	
}

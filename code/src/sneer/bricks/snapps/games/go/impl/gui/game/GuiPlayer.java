package sneer.bricks.snapps.games.go.impl.gui.game;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import sneer.bricks.snapps.games.go.impl.Player;
import sneer.bricks.snapps.games.go.impl.TimerFactory;
import sneer.bricks.snapps.games.go.impl.logging.GoLogger;
import sneer.bricks.snapps.games.go.impl.logic.BoardListener;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;
import sneer.bricks.snapps.games.go.impl.logic.Move;
import basis.lang.Closure;

public class GuiPlayer extends JFrame implements BoardListener,Player{
	
	private static final long serialVersionUID = 1L;
	private final StoneColor _side;
	private GoBoardPanel _goBoardPanel;
	private ActionsPanel actionsPanel;
	private GoScorePanel scorePanel;
	private Player _adversary;
	private final int _boardSize;
	private final int _gameID;
	
	public GuiPlayer(final int gameID,StoneColor side,final int boardSize, final TimerFactory timerFactory) {
		this._gameID = gameID;
		_side = side;
		this._boardSize = boardSize;
	
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Go - " + _side.name());	  
	    setResizable(true);
	    addAllComponents(timerFactory); 
	    setVisible(true);
	    int bord=getInsets().left+getInsets().right;
	    final int width = (int) ((_boardSize*_goBoardPanel.getCellSize())+_goBoardPanel.getCellSize()+bord);
		final int whatsLeftOfTheFrame = 70;
		setBounds(0, 0,(width<500)?500:width, (width<500)?500+whatsLeftOfTheFrame:width+whatsLeftOfTheFrame);
		
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				System.out.println(arg0.getKeyCode());
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				System.out.println(arg0.getKeyCode());
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				System.out.println(arg0.getKeyCode());
			}
		});
	}
	
	@Override
	public void updateScore(int blackScore, int whiteScore) {
		scorePanel.updateScore(blackScore, whiteScore);
	}

	@Override
	public void nextToPlay(StoneColor nextToPlay) {
		actionsPanel.nextToPlay(nextToPlay);
		_goBoardPanel.repaint();
	}

	@Override
	public void setAdversary(Player adversary) {
		GoLogger.log("GoFrame.setAdversary("+adversary+")");
		_adversary = adversary;
	}
	
	public void doMovePass() {
		GoLogger.log("GoFrame.doMovePass()");
		Move move = new Move(false, true, 0, 0, false,_gameID);
		_adversary.play(move);
	}
	
	public void doMoveResign() {
		GoLogger.log("GoFrame.doMoveResign();");
		_goBoardPanel.setLostByReign();
		Move move = new Move(true, false, 0, 0, false,_gameID);
		_adversary.play(move);
	}
	
	public void doMoveAddStone(int x, int y) {
		GoLogger.log("GoFrame.doMoveAddStone("+x+","+y+");");
		Move move = new Move(false, false, x,y, false,_gameID);
		_adversary.play(move);
	}

	public void doMoveMarkStone(int x, int y) {
		GoLogger.log("GoFrame.doMoveMarkStone("+x+","+y+");");
		Move move = new Move(false, false, x,y, true,_gameID);
		_adversary.play(move);
	}

	@Override
	public void play(Move move) {
		if(move.gameId != _gameID) return;
			
		if (move.isResign) {
			_goBoardPanel.receiveMoveResign();
			return;
		}
		if (move.isPass){
			_goBoardPanel.receiveMovePassTurn();
			return;
		}
		
		if (move.isMark){ 
			_goBoardPanel.receiveMoveMarkStone(move.xCoordinate, move.yCoordinate);
			return;
		}
		
		_goBoardPanel.receiveMoveAddStone(move.xCoordinate, move.yCoordinate);			
	}

	private void addAllComponents(final TimerFactory timerFactory) {
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		_goBoardPanel = new GoBoardPanel(this,timerFactory,_boardSize, _side);
		_goBoardPanel.setBoardListener(this);
		contentPane.add(_goBoardPanel, BorderLayout.CENTER);
		
		JPanel goEastPanel = new JPanel();
		goEastPanel.setLayout(new FlowLayout());
		scorePanel = new GoScorePanel(_goBoardPanel.scoreBlack(), _goBoardPanel.scoreWhite());
		goEastPanel.add(scorePanel);
		
		JSeparator space= new JSeparator(SwingConstants.VERTICAL);
		space.setPreferredSize(new Dimension(30,0));
		goEastPanel.add(space); 
		
		Closure pass = new Closure() { @Override public void run() {
			doMovePass();
		}};
		Closure resign = new Closure() { @Override public void run() {
			doMoveResign();
		}};
		actionsPanel = new ActionsPanel(pass,resign, _side);
		goEastPanel.add(actionsPanel);
				
		contentPane.add(goEastPanel, BorderLayout.SOUTH);
	}
	
}

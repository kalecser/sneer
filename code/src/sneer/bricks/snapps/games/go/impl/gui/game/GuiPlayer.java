package sneer.bricks.snapps.games.go.impl.gui.game;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.plaf.metal.MetalLookAndFeel;

import sneer.bricks.snapps.games.go.impl.Player;
import sneer.bricks.snapps.games.go.impl.TimerFactory;
import sneer.bricks.snapps.games.go.impl.gui.game.painters.RegularGoBoardImagePainter;
import sneer.bricks.snapps.games.go.impl.gui.game.painters.ToroidalBoardImagePainter;
import sneer.bricks.snapps.games.go.impl.logging.GoLogger;
import sneer.bricks.snapps.games.go.impl.logic.BoardListener;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard;
import sneer.bricks.snapps.games.go.impl.logic.ToroidalGoBoard;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;
import sneer.bricks.snapps.games.go.impl.logic.Move;

public class GuiPlayer extends JFrame implements BoardListener,Player{
	
	private static final long serialVersionUID = 1L;
	private final StoneColor _side;
	private GoBoardPanel _goBoardPanel;
	private Player _adversary;
	private final int _boardSize;
	private final int _gameID;
	
	public GuiPlayer(final int gameID,StoneColor side,final int boardSize, final TimerFactory timerFactory) {
		
		final LookAndFeel defaultLookAndFeel = UIManager.getLookAndFeel();
		
		settLookAndFeel(new MetalLookAndFeel());
		
		this._gameID = gameID;
		_side = side;
		this._boardSize = boardSize;
	
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Go - " + _side.name());	  
	    setResizable(true);
	    addAllComponents(timerFactory);
	    setVisible(true);
		setBounds(0, 0,800,600);
		
		nextToPlay(StoneColor.BLACK);
		
		settLookAndFeel(defaultLookAndFeel);
	}

	private void settLookAndFeel(LookAndFeel laf) {
		try {
			UIManager.setLookAndFeel(laf);
		} catch (UnsupportedLookAndFeelException e) {
			throw new basis.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}
	
	@Override
	public void updateScore(int blackScore, int whiteScore) {
		_goBoardPanel.updateScore(blackScore, whiteScore);
	}

	@Override
	public void nextToPlay(StoneColor nextToPlay) {
		_goBoardPanel.nextToPlay(nextToPlay);
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
		_goBoardPanel = new GoBoardPanel(this,new ToroidalGoBoard(_boardSize),timerFactory, new ToroidalBoardImagePainter(), _side, new MutableOffset());
//		_goBoardPanel = new GoBoardPanel(this,new GoBoard(_boardSize),timerFactory, new RegularGoBoardImagePainter(), _side);
		
		_goBoardPanel.setBoardListener(this);
		contentPane.add(_goBoardPanel, BorderLayout.CENTER);
	}

	public void gameEnded() {
		_goBoardPanel.setGameEnded();
	}
	
}

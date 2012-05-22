package sneer.bricks.snapps.games.go.gui;

import static basis.environments.Environments.my;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.snapps.games.go.GoBoard;
import sneer.bricks.snapps.games.go.GoBoard.StoneColor;
import sneer.bricks.snapps.games.go.Move;
import sneer.bricks.snapps.games.go.ToroidalGoBoard;
import sneer.bricks.snapps.games.go.gui.graphics.Board;
import sneer.bricks.snapps.games.go.gui.graphics.HUD;
import sneer.bricks.snapps.games.go.gui.graphics.HoverStone;
import sneer.bricks.snapps.games.go.gui.graphics.StonePainter;
import sneer.bricks.snapps.games.go.gui.graphics.StonesInPlay;
import basis.environments.Environment;
import basis.environments.Environments;
import basis.environments.ProxyInEnvironment;
import basis.lang.Closure;
import basis.lang.Consumer;

public class GoBoardPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	public static final int BOARD_SIZE = 5;
	public static final int SCREEN_SIZE = 500;
	public static final float MARGIN = SCREEN_SIZE/5;
	public static final float BOARD_IMAGE_SIZE = SCREEN_SIZE - MARGIN*2;
	public static final float CELL_SIZE = BOARD_IMAGE_SIZE/(BOARD_SIZE-1);
	public static final float STONE_DIAMETER = CELL_SIZE *0.97f;

	
	public class Scroller implements Runnable {

		@Override
		public void run() {
			if(!_isScrolling) return;
			scroll();
			if (_scrollXDelta != 0 || _scrollYDelta != 0) repaint();
		}

		private void scroll() {
			_scrollX = (_scrollX + _scrollXDelta + BOARD_SIZE) % BOARD_SIZE;
			_scrollY = (_scrollY + _scrollYDelta + BOARD_SIZE) % BOARD_SIZE;
		}
	}

	private final Environment _environment = my(Environment.class);

	final GoBoard _board = new ToroidalGoBoard(BOARD_SIZE);

	private BufferedImage _bufferImage;
	
	private boolean isWinner=false;

	private int _hoverX;
	private int _hoverY;

	private volatile boolean _isScrolling;
	private volatile int _scrollY;
	private volatile int _scrollX;
	private volatile int _scrollYDelta;
	private volatile int _scrollXDelta;

	private final Register<Move> _moveRegister;
	private final StoneColor _side;

	@SuppressWarnings("unused") private final WeakContract _refToAvoidGc;
	@SuppressWarnings("unused") private final WeakContract _refToAvoidGc2;

	private Board board;
	private HoverStone hoverStone;
	private StonesInPlay stonesInPlay;
	private HUD hud;

	public GoBoardPanel(Register<Move> moveRegister, StoneColor side) {
		board = new Board();
		final StonePainter stonePainter = new StonePainter();
		hoverStone = new HoverStone(stonePainter);
		stonesInPlay = new StonesInPlay(stonePainter);
		hud = new HUD();
		
		_side = side;
		_moveRegister = moveRegister;
		_refToAvoidGc = _moveRegister.output().addReceiver(new Consumer<Move>() { @Override public void consume(Move move) { 
			if (move == null) return; 
			play(move); 
		}});
		
		addMouseListener();
	    _refToAvoidGc2 = my(Timer.class).wakeUpEvery(150, new Scroller());		
	}
	
	private void play(Move move) {
		if (move.isResign) _board.resign();
		else {
			if (move.isPass) _board.passTurn();
			else {
				if (move.isMark) _board.toggleDeadStone(move.xCoordinate, move.yCoordinate);
				else _board.playStone(move.xCoordinate, move.yCoordinate);
			}
		}
		repaint();			
	}
	
	private void addMouseListener() {
		Object listener = ProxyInEnvironment.newInstance(new GoMouseListener());
		addMouseListener((MouseListener) listener);
	    addMouseMotionListener((MouseMotionListener) listener);
	}
	
	@Override
	public void paint(final Graphics graphics) {
		Environments.runWith(_environment, new Closure() { @Override public void run() {  //Refactor: Remove this when the gui nature is ready.
			paintInEnvironment(graphics);
		}});
	}

	
	private void paintInEnvironment(Graphics graphics) {
		Graphics2D buffer = getBuffer();
		
		board.draw(buffer);
		hoverStone.draw(buffer, _board, _hoverX, _hoverY, _scrollX, _scrollY);
		stonesInPlay.draw(buffer, _board, _scrollX, _scrollY);
				
		drawBoardOnAllSixCorners(graphics);
		drawCameraBoundaries(graphics);		
		
		int winState = HUD.NOONE_WIN;
		if (_board.nextToPlay()==null){
			int scW=scoreWhite().currentValue(),
					scB=scoreBlack().currentValue();
			if (scW==scB) return;
			if (_side==StoneColor.WHITE) isWinner=(scW>scB);
			else isWinner=(scW<scB);
			if(isWinner){
				winState = HUD.PLAYER_WIN;
			}else{
				winState = HUD.PLAYER_LOSES;
			}
			
		}
		hud.draw(graphics, winState);
	}

	private void drawCameraBoundaries(Graphics graphics) {
		graphics.setColor(Color.black);
		((Graphics2D) graphics).draw(new Rectangle2D.Float(MARGIN+1, MARGIN+1, BOARD_IMAGE_SIZE-2, BOARD_IMAGE_SIZE-2));
	}

	private void drawBoardOnAllSixCorners(Graphics graphics) {
		for (int i=0; i<9; i++) {
			int x=(int)(MARGIN+(BOARD_IMAGE_SIZE+CELL_SIZE)*((i % 3)-1));
			int y=(int)(MARGIN+(BOARD_IMAGE_SIZE+CELL_SIZE)*(Math.floor(i/3)-1));
			graphics.drawImage(_bufferImage, x, y, this);
		}
	}

	private int unscrollX(int x) { return (BOARD_SIZE + x - _scrollX) % BOARD_SIZE; }	
	private int unscrollY(int y) { return (BOARD_SIZE + y - _scrollY) % BOARD_SIZE; }
	
	private Graphics2D getBuffer() {
		_bufferImage = new BufferedImage((int)(BOARD_IMAGE_SIZE+CELL_SIZE), (int)(BOARD_IMAGE_SIZE+CELL_SIZE), 
			      BufferedImage.TYPE_INT_ARGB);
		return (Graphics2D)_bufferImage.getGraphics();
	}

	
	private int toScreenPosition(int coordinate) {
		float result = (coordinate - MARGIN + (CELL_SIZE / 2)) / CELL_SIZE;
		if (result < 0) return 0;
		if (result > BOARD_SIZE-1) return BOARD_SIZE-1;
		return (int)Math.floor(result);
	}
	

	public Signal<Integer> scoreWhite() {return _board.whiteScore();}
	public Signal<Integer> scoreBlack() {return _board.blackScore();}
	
	public Signal<StoneColor> nextToPlaySignal() {return _board.nextToPlaySignal();}

	public void passTurn() {
		_moveRegister.setter().consume(new Move(false, true, 0, 0, false));
	}
	public void resignTurn() {
		_moveRegister.setter().consume(new Move(true, false, 0, 0, false));
	}
	
	private class GoMouseListener extends MouseAdapter {
		
		private int scrollDeltaFor(int coordinate) {
			if (coordinate > (BOARD_SIZE-1) * CELL_SIZE + MARGIN) return -1;
			if (coordinate < MARGIN) return 1;
			return 0;
		}

		@Override public void mouseMoved(final MouseEvent e) {
			_scrollXDelta = scrollDeltaFor(e.getX());
			_scrollYDelta = scrollDeltaFor(e.getY());
			_hoverX = toScreenPosition(e.getX());
			_hoverY = toScreenPosition(e.getY());
			repaint();
		}
		
		@Override public void mouseReleased(MouseEvent e) {
			int x = unscrollX(toScreenPosition(e.getX()));
			int y = unscrollY(toScreenPosition(e.getY()));
			if (_board.nextToPlay()==null) {
				_moveRegister.setter().consume(new Move(false, false, x,y, true));
				return;
			}
			if (!_board.canPlayStone(x, y)) return;
			if (_side != _board.nextToPlay()) return;
			_moveRegister.setter().consume(new Move(false, false, x,y, false));
		}
		
		@Override public void mouseExited(MouseEvent e) {_isScrolling = false;}
		@Override public void mouseEntered(MouseEvent e) {_isScrolling = true;}
	}

}

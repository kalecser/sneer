package sneer.bricks.snapps.games.go.impl.gui;

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

import sneer.bricks.snapps.games.go.impl.gui.graphics.BoardPainter;
import sneer.bricks.snapps.games.go.impl.gui.graphics.HUDPainter;
import sneer.bricks.snapps.games.go.impl.gui.graphics.HoverStonePainter;
import sneer.bricks.snapps.games.go.impl.gui.graphics.StonePainter;
import sneer.bricks.snapps.games.go.impl.gui.graphics.StonesInPlayPainter;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;
import sneer.bricks.snapps.games.go.impl.logic.Move;
import sneer.bricks.snapps.games.go.impl.logic.ToroidalGoBoard;
import sneer.bricks.snapps.games.go.impl.network.RemoteBoard;
import sneer.bricks.snapps.games.go.impl.network.RemotePlayer;
import basis.environments.Environment;
import basis.environments.Environments;
import basis.environments.ProxyInEnvironment;
import basis.lang.Closure;

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
			System.out.println("RUN" + toString());
			scroll();
			if (_scrollingDirection != DIRECTION.STOPPED){
				repaint();
			}
		}

		private void scroll() {
			
			if (_scrollingDirection == DIRECTION.LEFT){
				scrollOnePieceToTheLeft();
			}
			if (_scrollingDirection == DIRECTION.RIGHT){
				scrollOnePieceToTheRight();
			}
			if (_scrollingDirection == DIRECTION.UP){
				scrollOnePieceToTheTop();
			}
			if (_scrollingDirection == DIRECTION.DOWN){
				scrollOnePieceToTheBottom();
			}
		}

		
	}

	private final Environment _environment = my(Environment.class);

	final GoBoard _board = new ToroidalGoBoard(BOARD_SIZE);

	private BufferedImage _bufferImage;
	
	private boolean isWinner=false;

	private int _hoverX;
	private int _hoverY;

	private volatile int _yOffsetMeasuredByPieces;
	private volatile int _xOffsetMeasuredByPieces;
	public static enum DIRECTION { STOPPED,UP,DOWN,LEFT,RIGHT;}
	private volatile DIRECTION _scrollingDirection;

	private final StoneColor _side;

	private BoardPainter _boardPainter;
	private HoverStonePainter _hoverStonePainter;
	private StonesInPlayPainter _stonesInPlayPainter;
	private HUDPainter _hudPainter;

	private final RemotePlayer _remotePlayer;

	private final RemoteBoard _remoteBoard;
	
	public GoBoardPanel(final RemotePlayer remotePlayer, final RemoteBoard remoteBoard,final TimerFactory timerFactory, StoneColor side) {
		_remotePlayer = remotePlayer;
		_remoteBoard = remoteBoard;
		_boardPainter = new BoardPainter();
		final StonePainter stonePainter = new StonePainter();
		_hoverStonePainter = new HoverStonePainter(stonePainter);
		_stonesInPlayPainter = new StonesInPlayPainter(stonePainter);
		_hudPainter = new HUDPainter();
		
		_side = side;
		_remotePlayer.setBoard(this);
		
		addMouseListener();
		timerFactory.wakeUpEvery(150, new Scroller());    	
	}
	
	private void scrollOnePieceToTheRight() {
		scrollPiecesHorizontally(1);
	}
	
	private void scrollOnePieceToTheLeft() {
		scrollPiecesHorizontally(-1);
	}
	
	private void scrollOnePieceToTheTop() {
		scrollPiecesVerticaly(-1);
	}
	
	private void scrollOnePieceToTheBottom() {
		scrollPiecesVerticaly(1);
	}
	
	private void scrollPiecesHorizontally(int scrollXDelta) {
		_xOffsetMeasuredByPieces = (_xOffsetMeasuredByPieces + scrollXDelta + BOARD_SIZE) % BOARD_SIZE;
	}
	
	private void scrollPiecesVerticaly(int scrollYDelta) {
		_yOffsetMeasuredByPieces = (_yOffsetMeasuredByPieces + scrollYDelta + BOARD_SIZE) % BOARD_SIZE;
	}

	public void play(Move move) {
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
		
		_boardPainter.draw(buffer);
		_hoverStonePainter.draw(buffer, _board, _hoverX, _hoverY, _xOffsetMeasuredByPieces, _yOffsetMeasuredByPieces);
		_stonesInPlayPainter.draw(buffer, _board, _xOffsetMeasuredByPieces, _yOffsetMeasuredByPieces);
				
		drawBoardOnAllSixCorners(graphics);
		drawCameraBoundaries(graphics);		
		
		int winState = HUDPainter.NOONE_WIN;
		if (_board.nextToPlay()==null){
			int scW=scoreWhite(),scB=scoreBlack();
			if (scW==scB) return;
			if (_side==StoneColor.WHITE) isWinner=(scW>scB);
			else isWinner=(scW<scB);
			if(isWinner){
				winState = HUDPainter.PLAYER_WIN;
			}else{
				winState = HUDPainter.PLAYER_LOSES;
			}
			
		}
		_hudPainter.draw(graphics, winState);
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

	private int unscrollX(int x) { 
		return (BOARD_SIZE + x - _xOffsetMeasuredByPieces) % BOARD_SIZE; 
	}
	
	private int unscrollY(int y) { 
		return (BOARD_SIZE + y - _yOffsetMeasuredByPieces) % BOARD_SIZE; 
	}
	
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
	

	public int scoreWhite() {
		return _board.whiteScore();
	}
	
	public int scoreBlack() {
		return _board.blackScore();
	}
	
	public StoneColor nextToPlaySignal() {
		return _board.nextToPlaySignal();
	}

	public void passTurn() {
		Move move = new Move(false, true, 0, 0, false);
		_remoteBoard.play(move);
	}
	
	public void resignTurn() {
		Move move = new Move(true, false, 0, 0, false);
		_remoteBoard.play(move);
	}
	
	private class GoMouseListener extends MouseAdapter {
		@Override 
		public void mouseMoved(final MouseEvent e) {
			_scrollingDirection = getScrollingDirection(e.getX(),e.getY());
			_hoverX = toScreenPosition(e.getX());
			_hoverY = toScreenPosition(e.getY());
			repaint();
		}
		
		private DIRECTION getScrollingDirection(int x, int y) {
			float bottomRightMargin = ((BOARD_SIZE-1) * CELL_SIZE) + MARGIN;
			if (x > bottomRightMargin) return DIRECTION.RIGHT;
			if (x < MARGIN) return DIRECTION.LEFT;
			if (y > bottomRightMargin) return DIRECTION.DOWN;
			if (y < MARGIN) return DIRECTION.UP;
			return DIRECTION.STOPPED;
		}

		@Override 
		public void mouseReleased(MouseEvent e) {
			int x = unscrollX(toScreenPosition(e.getX()));
			int y = unscrollY(toScreenPosition(e.getY()));
			if (_board.nextToPlay()==null) {
				Move move = new Move(false, false, x,y, true);
				_remoteBoard.play(move);
				return;
			}
			if (!_board.canPlayStone(x, y)) return;
			if (_side != _board.nextToPlay()) return;
			Move move = new Move(false, false, x,y, false);
			_remoteBoard.play(move);
		}
		
		@Override 
		public void mouseExited(MouseEvent e) {
			_scrollingDirection = DIRECTION.STOPPED;
		}
	}

	public void addScoreChangeListener(ScoreChangeListener scoreChangeListener) {
		_board.addScoreChangeListener(scoreChangeListener);
	}

	public void addNextToPlayListener(NextToPlayListeter nextToPlayListeter) {
		_board.addNextToPlayListener(nextToPlayListeter);
	}

}

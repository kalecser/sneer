package sneer.bricks.snapps.games.go.impl.gui;

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

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.snapps.games.go.impl.TimerFactory;
import sneer.bricks.snapps.games.go.impl.gui.graphics.BoardPainter;
import sneer.bricks.snapps.games.go.impl.gui.graphics.HUDPainter;
import sneer.bricks.snapps.games.go.impl.gui.graphics.HoverStonePainter;
import sneer.bricks.snapps.games.go.impl.gui.graphics.StonePainter;
import sneer.bricks.snapps.games.go.impl.gui.graphics.StonesInPlayPainter;
import sneer.bricks.snapps.games.go.impl.logging.GoLogger;
import sneer.bricks.snapps.games.go.impl.logic.BoardListener;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;
import sneer.bricks.snapps.games.go.impl.logic.ToroidalGoBoard;
import basis.environments.ProxyInEnvironment;

public class GoBoardPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;

	public static final int BOARD_SIZE = 5;
	public static final int SCREEN_SIZE = 500;
	public static final float MARGIN = SCREEN_SIZE/5;
	public static final float BOARD_IMAGE_SIZE = SCREEN_SIZE - MARGIN*2;
	public static final float CELL_SIZE = BOARD_IMAGE_SIZE/(BOARD_SIZE-1);
	public static final float STONE_DIAMETER = CELL_SIZE *0.97f;

	public static enum DIRECTION { STOPPED,UP,DOWN,LEFT,RIGHT;}
	private DIRECTION _scrollingDirection = DIRECTION.STOPPED;

	final GoBoard _board = new ToroidalGoBoard(BOARD_SIZE);

	private BufferedImage _bufferImage;
	private volatile int _yOffsetMeasuredByPieces;
	private volatile int _xOffsetMeasuredByPieces;

	private final StoneColor _side;

	private BoardPainter _boardPainter;
	private HoverStonePainter _hoverStonePainter;
	private StonesInPlayPainter _stonesInPlayPainter;
	private HUDPainter _hudPainter;

	@SuppressWarnings("unused")
	private WeakContract _referenceToAvoidGc;

	private final GuiPlayer _goFrame;
	
	public GoBoardPanel(final GuiPlayer goFrame,final TimerFactory timerFactory, StoneColor side) {
		_goFrame = goFrame;
		_boardPainter = new BoardPainter();
		final StonePainter stonePainter = new StonePainter();
		_hoverStonePainter = new HoverStonePainter(stonePainter);
		_stonesInPlayPainter = new StonesInPlayPainter(stonePainter);
		_hudPainter = new HUDPainter();
		
		_side = side;
		
		addMouseListener();
		_referenceToAvoidGc = timerFactory.wakeUpEvery(150, new Runnable() {@Override public void run() {
			scroll();
		}});    	
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
		_hoverStonePainter.setOffset( _xOffsetMeasuredByPieces, _yOffsetMeasuredByPieces);
		_stonesInPlayPainter.setOffset( _xOffsetMeasuredByPieces, _yOffsetMeasuredByPieces);
	}
	
	private void scrollPiecesVerticaly(int scrollYDelta) {
		_yOffsetMeasuredByPieces = (_yOffsetMeasuredByPieces + scrollYDelta + BOARD_SIZE) % BOARD_SIZE;
		_hoverStonePainter.setOffset( _xOffsetMeasuredByPieces, _yOffsetMeasuredByPieces);
		_stonesInPlayPainter.setOffset( _xOffsetMeasuredByPieces, _yOffsetMeasuredByPieces);
	}

	void receiveMoveAddStone(int xCoordinate, int yCoordinate) {
		GoLogger.log("GoBoardPanel.receiveMoveAddStone("+xCoordinate+","+yCoordinate+")");
		_board.playStone(xCoordinate, yCoordinate);
		repaint();
	}

	void receiveMoveMarkStone(int xCoordinate, int yCoordinate) {
		GoLogger.log("GoBoardPanel.receiveMoveMarkStone("+xCoordinate+","+yCoordinate+")");
		_board.toggleDeadStone(xCoordinate, yCoordinate);
		repaint();
	}

	void receiveMovePassTurn() {
		GoLogger.log("GoBoardPanel.receiveMovePassTurn()");
		_board.passTurn();
		repaint();
	}

	void receiveMoveResign() {
		GoLogger.log("GoBoardPanel.receiveMoveResign()");
		_board.resign();
		repaint();
	}
	
	public StoneColor nextToPlaySignal() {
		return _board.nextToPlaySignal();
	}

	private void doMoveAddStone(int x, int y) {
		GoLogger.log("GoBoardPanel.doMoveAddStone("+x+","+y+")");
		_goFrame.doMoveAddStone(x,y);
	}
	
	private void doMoveMarkStone(int x, int y) {
		GoLogger.log("GoBoardPanel.doMoveMarkStone("+x+","+y+")");
		_goFrame.doMoveMarkStone(x,y);
	}
	
	private void addMouseListener() {
		Object listener = ProxyInEnvironment.newInstance(new GoMouseListener());
		addMouseListener((MouseListener) listener);
	    addMouseMotionListener((MouseMotionListener) listener);
	}
	
	private void scroll() {
		if (_scrollingDirection == DIRECTION.STOPPED){
			return;
		}
		
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
		repaint();
	}
	
	private void setDirection(DIRECTION scrollingDirection) {
		_scrollingDirection = scrollingDirection;
	}
	
	@Override
	public void paint(final Graphics graphics) {
		Graphics2D buffer = getBuffer();
		
		_boardPainter.draw(buffer);
		_hoverStonePainter.draw(buffer, _board);
		_stonesInPlayPainter.draw(buffer, _board);
				
		drawBoardOnAllSixCorners(graphics);
		drawCameraBoundaries(graphics);		
		
		gameLogicDecideWinner();//TODO: move this out of the paint method
		
		_hudPainter.draw(graphics);
	}

	private void gameLogicDecideWinner() {
		int winState = HUDPainter.NOONE_WIN;
		if (_board.nextToPlay()==null){
			int scoreWhite=scoreWhite();
			int scoreBlack=scoreBlack();
			boolean isNotADraw = scoreWhite!=scoreBlack;
			if (isNotADraw){
				boolean isWinner=false;
				if (_side==StoneColor.WHITE){
					isWinner=(scoreWhite>scoreBlack);
				}else{
					isWinner=(scoreWhite<scoreBlack);
				}
				if(isWinner){
					winState = HUDPainter.PLAYER_WIN;
				}else{
					winState = HUDPainter.PLAYER_LOSES;
				}
			}
		}
		_hudPainter.setWinState(winState);
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
	
	private class GoMouseListener extends MouseAdapter {
		@Override 
		public void mouseMoved(final MouseEvent e) {
			setDirection(getScrollingDirection(e.getX(),e.getY()));
			_hoverStonePainter.setHoverX(toScreenPosition(e.getX()));
			_hoverStonePainter.setHoverY(toScreenPosition(e.getY()));
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
				doMoveMarkStone(x, y);
				return;
			}
			if (!_board.canPlayStone(x, y)) return;
			if (_side != _board.nextToPlay()) return;
			doMoveAddStone(x, y);
		}

		
		@Override 
		public void mouseExited(MouseEvent e) {
			setDirection(DIRECTION.STOPPED);
		}
	}

	public void setBoardListener(BoardListener boardListener) {
		_board.setBoardListener(boardListener);
	}

}

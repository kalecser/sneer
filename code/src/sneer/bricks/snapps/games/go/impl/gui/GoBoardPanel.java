package sneer.bricks.snapps.games.go.impl.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

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

	private static final float CELL_MAX_SIZE = 100;
	private static final float CELL_MIN_SIZE = 5;

	private int _boardSize;
	private float _boardImageSize;
	private float _cellSize;
	final GoBoard _board;

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

	private StonePainter _stonePainter;
	
	public GoBoardPanel(final GuiPlayer goFrame,final TimerFactory timerFactory, StoneColor side) {
		_goFrame = goFrame;		
		_side = side;
		
		_boardSize = 15;
		_board = new ToroidalGoBoard(_boardSize);
		
		createPainters();
		
		addMouseListener();
		_referenceToAvoidGc = timerFactory.wakeUpEvery(150, new Runnable() {@Override public void run() {
			repaint();
		}});    	
	}

	@Override
	public void paint(final Graphics graphics) {
		Graphics2D buffer = getBuffer();
		_boardPainter.draw(buffer);
		_hoverStonePainter.draw(buffer, _board);
		_stonesInPlayPainter.draw(buffer, _board);
		drawBoardTiled(graphics);
		_hudPainter.draw(graphics);
	}

	public int scoreWhite() {
		return _board.whiteScore();
	}

	public int scoreBlack() {
		return _board.blackScore();
	}

	public void setBoardListener(BoardListener boardListener) {
		_board.setBoardListener(boardListener);
	}

	void receiveMoveAddStone(int xCoordinate, int yCoordinate) {
		GoLogger.log("GoBoardPanel.receiveMoveAddStone("+xCoordinate+","+yCoordinate+")");
		_board.playStone(xCoordinate, yCoordinate);
		decideWinner();
	}

	void receiveMoveMarkStone(int xCoordinate, int yCoordinate) {
		GoLogger.log("GoBoardPanel.receiveMoveMarkStone("+xCoordinate+","+yCoordinate+")");
		_board.toggleDeadStone(xCoordinate, yCoordinate);
		decideWinner();
	}

	void receiveMovePassTurn() {
		GoLogger.log("GoBoardPanel.receiveMovePassTurn()");
		_board.passTurn();
		decideWinner();
	}

	void receiveMoveResign() {
		GoLogger.log("GoBoardPanel.receiveMoveResign()");
		_board.resign();
		decideWinner();
	}

	private void createPainters() {
		_cellSize = 40;
		_boardImageSize = _cellSize*(_boardSize-1);
		
		_boardPainter = new BoardPainter(_boardSize, _boardImageSize, _cellSize);
		_stonePainter = new StonePainter(_boardImageSize, _cellSize);
		_hoverStonePainter = new HoverStonePainter(_stonePainter,_boardSize, _cellSize);		
		_stonesInPlayPainter = new StonesInPlayPainter(_stonePainter,_cellSize);
		_hudPainter = new HUDPainter();
	}
	
	private void updateCellSize(int i) {
		final float newCellSize = _cellSize + i;
		if(newCellSize > CELL_MAX_SIZE) return;
		if(newCellSize < CELL_MIN_SIZE) return;
		
		_cellSize = newCellSize;
		_boardImageSize = _cellSize*(_boardSize-1);
		_boardPainter.setBoardDimensions(_boardSize, _boardImageSize, _cellSize);
		_stonePainter.setBoardDimensions(_boardImageSize, _cellSize);
		_hoverStonePainter.setBoardDimensions(_boardSize, _cellSize);
		_stonesInPlayPainter.setBoardDimensions(_cellSize);
	}
	private void doMoveAddStone(int x, int y) {
		GoLogger.log("GoBoardPanel.doMoveAddStone("+x+","+y+")");
		_goFrame.doMoveAddStone(x,y);
		decideWinner();
	}
	
	private void doMoveMarkStone(int x, int y) {
		GoLogger.log("GoBoardPanel.doMoveMarkStone("+x+","+y+")");
		_goFrame.doMoveMarkStone(x,y);
		decideWinner();
	}
	
	private void addMouseListener() {
		Object listener = ProxyInEnvironment.newInstance(new GoMouseListener());
		addMouseListener((MouseListener) listener);
	    addMouseMotionListener((MouseMotionListener) listener);
	    addMouseWheelListener((MouseWheelListener) listener);
	}
	
	private void decideWinner() {
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

	private void drawBoardTiled(Graphics graphics) {
		final Rectangle clipBounds = graphics.getClipBounds();
		int x_ = 0;
		int y_ = 0;
		
		graphics.drawImage(_bufferImage, x_, y_, this);
		
		while(clipBounds.contains(x_, y_)){
			while(clipBounds.contains(x_, y_)){
				graphics.drawImage(_bufferImage, x_, y_, this);
				x_ += (_boardImageSize+1);
			}
			x_ = 0;
			y_ += (_boardImageSize+1);
		}
	}

	private int unscrollX(int x) { 
		return (_boardSize + x - _xOffsetMeasuredByPieces) % _boardSize; 
	}
	
	private int unscrollY(int y) { 
		return (_boardSize + y - _yOffsetMeasuredByPieces) % _boardSize; 
	}
	
	private Graphics2D getBuffer() {
		_bufferImage = new BufferedImage((int)(_boardImageSize+_cellSize), (int)(_boardImageSize+_cellSize), 
			      BufferedImage.TYPE_INT_ARGB);
		return (Graphics2D)_bufferImage.getGraphics();
	}

	
	private int toScreenPosition(final int coordinate) {
		int coordinateInsideBoard = (int) (coordinate %  _boardImageSize);
		float result = (coordinateInsideBoard -  (_cellSize / 2)) / _cellSize;
		return (int)Math.ceil(result)%_boardSize;
	}
	

	private class GoMouseListener extends MouseAdapter {
		private int _startX;
		private int _startY;

		@Override 
		public void mouseMoved(final MouseEvent e) {
			_hoverStonePainter.setHoverX(toScreenPosition(e.getX()));
			_hoverStonePainter.setHoverY(toScreenPosition(e.getY()));
			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if(!SwingUtilities.isMiddleMouseButton(e)) return;
			final int dragX = e.getX() - _startX;
			final int dragY = e.getY() - _startY;
			System.out.println("Drag "+dragX+" "+dragY);
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			if(SwingUtilities.isMiddleMouseButton(e)){
				_startX = e.getX();
				_startY = e.getY();
			}
		}
		
		@Override 
		public void mouseReleased(MouseEvent e) {
			if(SwingUtilities.isMiddleMouseButton(e)) return;
			
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
		public void mouseWheelMoved(MouseWheelEvent e) {
			final int wheelRotation = e.getWheelRotation();
			int factor = 2;
			updateCellSize(wheelRotation*factor*-1);
		}
	}

}

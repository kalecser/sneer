package sneer.bricks.snapps.games.go.impl.gui.game;

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
import sneer.bricks.snapps.games.go.impl.gui.game.painters.BoardPainter;
import sneer.bricks.snapps.games.go.impl.gui.game.painters.HUDPainter;
import sneer.bricks.snapps.games.go.impl.gui.game.painters.HoverStonePainter;
import sneer.bricks.snapps.games.go.impl.gui.game.painters.StonePainter;
import sneer.bricks.snapps.games.go.impl.gui.game.painters.StonesInPlayPainter;
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
	private final Rectangle _boardImageRectangle = new Rectangle(0,0,(int)_boardImageSize,(int)_boardImageSize);
	private float _cellSize;
	private final GoBoard _board;

	private BufferedImage _bufferImage;

	private final StoneColor _side;

	private BoardPainter _boardPainter;
	private HoverStonePainter _hoverStonePainter;
	private StonesInPlayPainter _stonesInPlayPainter;
	private HUDPainter _hudPainter;
	
	private int _xOffset;
	private int _yOffset;

	@SuppressWarnings("unused")
	private WeakContract _referenceToAvoidGc;

	private final GuiPlayer _goFrame;

	private StonePainter _stonePainter;

	private boolean _hasResigned = false;
	private boolean _otherResigned = false;
	
	public GoBoardPanel(final GuiPlayer goFrame,final TimerFactory timerFactory,final int boardSize, StoneColor side) {
		_goFrame = goFrame;		
		_side = side;
		
		_boardSize = boardSize;
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

	public float getCellSize() {
		return _cellSize;
	}

	public void setLostByReign() {
		_hasResigned = true;
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
		if(!_hasResigned)
			_otherResigned = true;
		GoLogger.log("GoBoardPanel.receiveMoveResign()");
		_board.resign();
		decideWinner();
	}

	private void createPainters() {
		_cellSize = 40;
		_boardImageSize = _cellSize*(_boardSize);
		_boardImageRectangle.width =(int) _boardImageSize;
		_boardImageRectangle.height =(int) _boardImageSize;
		_xOffset = (int) (_cellSize - _boardImageSize);
		_yOffset = (int) (_cellSize - _boardImageSize);
		
		_boardPainter = new BoardPainter(_boardSize, _boardImageSize, _cellSize);
		_stonePainter = new StonePainter(_boardImageSize, _cellSize);
		_hoverStonePainter = new HoverStonePainter(_stonePainter,_boardSize, _cellSize);		
		_stonesInPlayPainter = new StonesInPlayPainter(_stonePainter,_cellSize);
		_hudPainter = new HUDPainter();
	}
	
	private void updateCellSize(int add) {
		float newCellSize = _cellSize + add;
		if(newCellSize > CELL_MAX_SIZE){
			newCellSize = CELL_MAX_SIZE;
		}
		if(newCellSize < CELL_MIN_SIZE){
			newCellSize = CELL_MIN_SIZE;
		}
		
		float oldBoardImageSize = _boardImageSize;
		
		_cellSize = newCellSize;
		_boardImageSize = _cellSize*(_boardSize);
		_boardImageRectangle.width =(int) _boardImageSize;
		_boardImageRectangle.height =(int) _boardImageSize;
		
		_xOffset = (int) ((_xOffset / oldBoardImageSize)*_boardImageSize);
		_yOffset = (int) ((_yOffset / oldBoardImageSize)*_boardImageSize);
		
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
		
		if(_hasResigned){
			winState = HUDPainter.PLAYER_LOSES;
		}
		if(_otherResigned){
			winState = HUDPainter.PLAYER_WIN;
		}
		
		_hudPainter.setWinState(winState);
		
	}

	private void drawBoardTiled(Graphics graphics) {
		final Rectangle clipBounds = graphics.getClipBounds();
		
		_boardImageRectangle.x = _xOffset;
		_boardImageRectangle.y = _yOffset;
		int count = 0;
		
		while(clipBounds.intersects(_boardImageRectangle)){
			while(clipBounds.intersects(_boardImageRectangle)){
				count++;
				graphics.drawImage(_bufferImage, _boardImageRectangle.x, _boardImageRectangle.y, this);
				_boardImageRectangle.x += (_boardImageSize+1);
			}
			_boardImageRectangle.x = _xOffset;
			_boardImageRectangle.y += (_boardImageSize+1);
		}
		if(count == 0){
			System.out.println(count);
		}
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
			_hoverStonePainter.setHoverX(toScreenPosition(e.getX()-_xOffset));
			_hoverStonePainter.setHoverY(toScreenPosition(e.getY()-_yOffset));
			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if(!SwingUtilities.isMiddleMouseButton(e)) return;
			_xOffset += e.getX() - _startX - _boardImageSize;
			_xOffset = (int) (_xOffset % _boardImageSize);
			if(_xOffset > _boardImageSize){
				System.out.println();
			}
			_yOffset += e.getY() - _startY - _boardImageSize;
			_yOffset = (int) (_yOffset % _boardImageSize);
			_startX = e.getX();
			_startY = e.getY();
			repaint();
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
			
			int x = toScreenPosition(e.getX()-_xOffset);
			int y = toScreenPosition(e.getY()-_yOffset);
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

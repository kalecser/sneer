package spikes.klaus.go.gui;

import static sneer.foundation.environments.Environments.my;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.environments.ProxyInEnvironment;
import sneer.foundation.lang.Consumer;
import spikes.klaus.go.GoBoard;
import spikes.klaus.go.Move;
import spikes.klaus.go.ToroidalGoBoard;
import spikes.klaus.go.GoBoard.StoneColor;

public class GoBoardPanel extends JPanel {
	
	static final int _boardSize=5;
	private static final int _SCREEN_SIZE = 500;
	private static final int _CELL_SIZE = 300/(_boardSize-1);
	volatile boolean _isScrolling;
	
	public class Scroller implements Runnable {

		public void run() {
			if(!_isScrolling) return;
			scrollX();
			scrollY();
			if (_scrollXDelta != 0 || _scrollYDelta != 0) repaint();
		}

		private void scrollX() {
			_scrollX = (_scrollX + _scrollXDelta + _boardSize) % _boardSize;
		}
		
		private void scrollY() {
			_scrollY = (_scrollY + _scrollYDelta + _boardSize) % _boardSize;
		}

	}

	private static final int _MARGIN = 100;
	private static final int _STONE_DIAMETER = _CELL_SIZE;
	private static final long serialVersionUID = 1L;

	private final GoBoard _board = new ToroidalGoBoard(_boardSize);

	private BufferedImage _bufferImage;

	private int _hoverX;
	private int _hoverY;

	private volatile int _scrollY;
	private volatile int _scrollX;
	private volatile int _scrollYDelta;
	private volatile int _scrollXDelta;

	private final Register<Move> _moveRegister;
	private final StoneColor _side;

	@SuppressWarnings("unused") private final WeakContract _refToAvoidGc;
	@SuppressWarnings("unused") private final WeakContract _refToAvoidGc2;

	public GoBoardPanel(Register<Move> moveRegister, StoneColor side) {
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
		if (move.isPass)
			_board.passTurn();
		else
			_board.playStone(move.xCoordinate, move.yCoordinate);
		
		repaint();			
	}
	
	private void addMouseListener() {
		Object listener = ProxyInEnvironment.newInstance(new GoMouseListener());
		addMouseListener((MouseListener) listener);
	    addMouseMotionListener((MouseMotionListener) listener);
	}
	
	@Override
	public void paint(Graphics graphics){
		Graphics2D buffer = getBuffer();
		
	    buffer.setColor(new Color(228,205,152,90));
		buffer.fillRect(0, 0, _SCREEN_SIZE, _SCREEN_SIZE);
		
		buffer.setColor(new Color(228,205,152));
		buffer.fillRect(_MARGIN, _MARGIN, _CELL_SIZE * (_boardSize-1), _CELL_SIZE * (_boardSize-1));
	    
		buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		buffer.setColor(Color.black);
	
		paintGrid(buffer);		
		drawHoverStone(buffer);
		paintStones(buffer);
		
		graphics.drawImage(_bufferImage, 0, 0, this);
	}

	private void drawHoverStone(Graphics2D graphics) {
		if (_hoverX == -1) return;

		if(_board.nextToPlay() == StoneColor.BLACK)
			graphics.setColor(new Color(0, 0, 0, 50));
		else	
			graphics.setColor(new Color(255, 255, 255, 90));
			
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		paintStoneOnCoordinates(graphics, toCoordinate(_hoverX), toCoordinate(_hoverY));

	}

	private void paintGrid(Graphics2D buffer) {
		int c=0;
		for(int i = 0; i < _boardSize; i ++ ){
			buffer.setColor(Color.black);
			buffer.drawLine(c+_MARGIN, 0+_MARGIN, c+_MARGIN, 300+_MARGIN);
			buffer.drawLine(0+_MARGIN, c+_MARGIN, 300+_MARGIN, c+_MARGIN);
			c+=_CELL_SIZE;
		}
	}

	private void paintStones(Graphics2D graphics) {
		int size = _board.size();

		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
				paintStoneOnPosition(graphics, x, y);
			
	}

	private void paintStoneOnPosition(Graphics2D graphics, int x, int y) {
		StoneColor color = _board.stoneAt(x, y);
		if (color == null) return;
		
		int cx = toCoordinate(scrollX(x));		
		int cy = toCoordinate(scrollY(y));		
	
		graphics.setColor(toAwtColor(color));
		paintStoneOnCoordinates(graphics, cx, cy);
	}

	private Color toAwtColor(StoneColor color) {
		return color == StoneColor.BLACK? Color.black: Color.white;
	}

	private int scrollX(int x) { return (x + _scrollX) % _boardSize; }
	private int unscrollX(int x) { return (_boardSize + x - _scrollX) % _boardSize; }

	private int scrollY(int y) { return (y + _scrollY) % _boardSize; }
	private int unscrollY(int y) { return (_boardSize + y - _scrollY) % _boardSize; }
	
	private Graphics2D getBuffer() {
		_bufferImage = (BufferedImage)createImage(_SCREEN_SIZE, _SCREEN_SIZE);
	    return _bufferImage.createGraphics();
	}


	private int toCoordinate(int position) {
		return position * _CELL_SIZE + _MARGIN;
	}
	
	private int toScreenPosition(int coordinate) {
		int result = (coordinate - _MARGIN + (_CELL_SIZE / 2)) / _CELL_SIZE;
		if (result < 0) return 0;
		if (result > _boardSize-1) return _boardSize-1;
		return result;
	}

	private class GoMouseListener extends MouseAdapter {
		
		@Override
		public void mouseMoved(final MouseEvent e) {
			_scrollXDelta = scrollDeltaFor(e.getX());
			_scrollYDelta = scrollDeltaFor(e.getY());
			
			_hoverX = toScreenPosition(e.getX());
			_hoverY = toScreenPosition(e.getY());
			
			if (!_board.canPlayStone(unscrollX(_hoverX), unscrollY(_hoverY)))
				_hoverX = _hoverY = -1;
			
			repaint();
		}

		private int scrollDeltaFor(int coordinate) {
			if (coordinate > _boardSize * _CELL_SIZE + _MARGIN) return -1;
			if (coordinate < _MARGIN) return 1;
			return 0;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			int x = unscrollX(toScreenPosition(e.getX()));
			int y = unscrollY(toScreenPosition(e.getY()));
			if (!_board.canPlayStone(x, y)) return;
			if (_side != _board.nextToPlay()) return;
			
			_moveRegister.setter().consume(new Move(false, false, x,y));
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			_isScrolling = false;
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			_isScrolling = true;
		}
		
	}

	private void paintStoneOnCoordinates(Graphics2D graphics, int x, int y) {
		graphics.fillOval(x - (_STONE_DIAMETER / 2), y - (_STONE_DIAMETER / 2), _STONE_DIAMETER, _STONE_DIAMETER);
	}
	
	public Signal<Integer> countCapturedBlack(){
		return _board.blackCapturedCount();
	}
	
	public Signal<Integer> countCapturedWhite(){
		return _board.whiteCapturedCount();
	}
	
	public void passTurn() {
		_moveRegister.setter().consume(new Move(false, true, 0, 0));
	}

	public Signal<StoneColor> nextToPlaySignal() {
		return _board.nextToPlaySignal();
	}

}

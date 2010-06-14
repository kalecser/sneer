package spikes.klaus.go.gui;

import static sneer.foundation.environments.Environments.my;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.environments.ProxyInEnvironment;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;
import spikes.klaus.go.GoBoard;
import spikes.klaus.go.Move;
import spikes.klaus.go.ToroidalGoBoard;
import spikes.klaus.go.GoBoard.StoneColor;

public class GoBoardPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private static final int BOARD_SIZE = 9;
	private static final int SCREEN_SIZE = 500;
	private static final float MARGIN = SCREEN_SIZE/5;
	private static final float BOARD_IMAGE_SIZE = SCREEN_SIZE - MARGIN*2;
	private static final float CELL_SIZE = BOARD_IMAGE_SIZE/(BOARD_SIZE-1);
	private static final float STONE_DIAMETER = CELL_SIZE *0.97f;

	
	public class Scroller implements Runnable {

		public void run() {
			if(!_isScrolling) return;
			scrollX();
			scrollY();
			if (_scrollXDelta != 0 || _scrollYDelta != 0) repaint();
		}

		private void scrollX() {
			_scrollX = (_scrollX + _scrollXDelta + BOARD_SIZE) % BOARD_SIZE;
		}
		
		private void scrollY() {
			_scrollY = (_scrollY + _scrollYDelta + BOARD_SIZE) % BOARD_SIZE;
		}

	}

	private final Environment _environment = my(Environment.class);

	private volatile boolean _isScrolling;

	final GoBoard _board = new ToroidalGoBoard(BOARD_SIZE);

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
		
	    buffer.setColor(new Color(228,205,152,90));
		buffer.fillRect(0, 0, SCREEN_SIZE, SCREEN_SIZE);
		
		buffer.setColor(new Color(228,205,152));
		buffer.fill(new Rectangle2D.Float(MARGIN, MARGIN, BOARD_IMAGE_SIZE, BOARD_IMAGE_SIZE));
	    
		buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		buffer.setColor(Color.black);
	
		paintGrid(buffer);		
		drawHoverStone(buffer);
		paintStones(buffer);
		
		graphics.drawImage(_bufferImage, 0, 0, this);
	}

	private void drawHoverStone(Graphics2D graphics) {
		if (!_board.canPlayStone(unscrollX(_hoverX), unscrollY(_hoverY)))
			return;

		if(_board.nextToPlay() == StoneColor.BLACK)
			graphics.setColor(new Color(0, 0, 0, 50));
		else	
			graphics.setColor(new Color(255, 255, 255, 90));
			
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		paintStoneOnCoordinates(graphics, toCoordinate(_hoverX), toCoordinate(_hoverY), false);

	}

	private void paintGrid(Graphics2D buffer) {
		float c = 0;
		for(int i = 0; i < BOARD_SIZE; i++ ) {
			buffer.setColor(Color.black);
			//method 1
			buffer.draw(new Line2D.Float(c+MARGIN, MARGIN, c+MARGIN, MARGIN+BOARD_IMAGE_SIZE));
			buffer.draw(new Line2D.Float(MARGIN, c+MARGIN, MARGIN+BOARD_IMAGE_SIZE, c+MARGIN));
			//method 2
			//buffer.drawLine((int)(c+MARGIN), (int)MARGIN, (int)(c+MARGIN), (int)(MARGIN+BOARD_IMAGE_SIZE));
			//buffer.drawLine((int)MARGIN, (int)(c+MARGIN), (int)(MARGIN+BOARD_IMAGE_SIZE), (int)(c+MARGIN));
			//method 3
			//buffer.drawRect((int)MARGIN, (int)MARGIN, (int)c, (int)c);
			//buffer.drawRect((int)(c+MARGIN), (int)(c+MARGIN), (int)(BOARD_IMAGE_SIZE-c), (int)(BOARD_IMAGE_SIZE-c));
			c += CELL_SIZE;
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
		boolean dead=false;
		if (color == null) {
			if (_board.nextToPlay()==null) {
				color = _board.getPrevColor(x, y);
				if (color==null) return;
				dead=true;
			}
			else return;
		}
		
		float cx = toCoordinate(scrollX(x));		
		float cy = toCoordinate(scrollY(y));		
	
		graphics.setColor(toAwtColor(color));
		paintStoneOnCoordinates(graphics, cx, cy, dead);
	}

	private Color toAwtColor(StoneColor color) {
		return color == StoneColor.BLACK? Color.black: Color.white;
	}

	private int scrollX(int x) { return (x + _scrollX) % BOARD_SIZE; }
	private int unscrollX(int x) { return (BOARD_SIZE + x - _scrollX) % BOARD_SIZE; }

	private int scrollY(int y) { return (y + _scrollY) % BOARD_SIZE; }
	private int unscrollY(int y) { return (BOARD_SIZE + y - _scrollY) % BOARD_SIZE; }
	
	private Graphics2D getBuffer() {
		_bufferImage = (BufferedImage)createImage(SCREEN_SIZE, SCREEN_SIZE);
	    return _bufferImage.createGraphics();
	}


	private float toCoordinate(int position) {
		return position * CELL_SIZE + MARGIN;
	}
	
	private int toScreenPosition(int coordinate) {
		float result = (coordinate - MARGIN + (CELL_SIZE / 2)) / CELL_SIZE;
		if (result < 0) return 0;
		if (result > BOARD_SIZE-1) return BOARD_SIZE-1;
		return (int)Math.floor(result);
	}

	private void paintStoneOnCoordinates(Graphics2D graphics, float x, float y, boolean dead) {
		float d = STONE_DIAMETER;
		if (dead) d*=0.6;
		
		Shape stone = new Ellipse2D.Float(x - (d / 2), y - (d / 2), d, d);
		graphics.fill(stone);
	}
	
	public Signal<Integer> scoreWhite(){
		return _board.whiteScore();
	}
	
	public Signal<Integer> scoreBlack(){
		return _board.blackScore();
	}
	
	public void passTurn() {
		_moveRegister.setter().consume(new Move(false, true, 0, 0, false));
	}
	
	public void resignTurn() {
		_moveRegister.setter().consume(new Move(true, false, 0, 0, false));
	}

	public Signal<StoneColor> nextToPlaySignal() {
		return _board.nextToPlaySignal();
	}
	
	private class GoMouseListener extends MouseAdapter {
		
		@Override
		public void mouseMoved(final MouseEvent e) {
			_scrollXDelta = scrollDeltaFor(e.getX());
			_scrollYDelta = scrollDeltaFor(e.getY());
			
			_hoverX = toScreenPosition(e.getX());
			_hoverY = toScreenPosition(e.getY());
			
			repaint();
		}
		
		private int scrollDeltaFor(int coordinate) {
			if (coordinate > (BOARD_SIZE-1) * CELL_SIZE + MARGIN) return -1;
			if (coordinate < MARGIN) return 1;
			return 0;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			int x = unscrollX(toScreenPosition(e.getX()));
			int y = unscrollY(toScreenPosition(e.getY()));
			if (_board.nextToPlay()==null) {
				_moveRegister.setter().consume(new Move(false, false, x,y, true));
				//repaint();
				return;
			}
			if (!_board.canPlayStone(x, y)) return;
			if (_side != _board.nextToPlay()) return;
			
			_moveRegister.setter().consume(new Move(false, false, x,y, false));
			
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

}

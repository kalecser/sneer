package sneer.bricks.snapps.games.go.gui;

import static sneer.foundation.environments.Environments.my;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
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
import sneer.bricks.snapps.games.go.GoBoard;
import sneer.bricks.snapps.games.go.Move;
import sneer.bricks.snapps.games.go.ToroidalGoBoard;
import sneer.bricks.snapps.games.go.GoBoard.StoneColor;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.environments.ProxyInEnvironment;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;

public class GoBoardPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private static final int BOARD_SIZE = 19;
	private static final int SCREEN_SIZE = 500;
	private static final float MARGIN = SCREEN_SIZE/5;
	private static final float BOARD_IMAGE_SIZE = SCREEN_SIZE - MARGIN*2;
	private static final float CELL_SIZE = BOARD_IMAGE_SIZE/(BOARD_SIZE-1);
	private static final float STONE_DIAMETER = CELL_SIZE *0.97f;

	
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

	private BufferedImage _bufferImage, _bufferGrid;
	
	private Image winImg, loseImg;//, blackStoneImg, whiteStoneImg;
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


	public GoBoardPanel(Register<Move> moveRegister, StoneColor side) {
		_side = side;
		_moveRegister = moveRegister;
		_refToAvoidGc = _moveRegister.output().addReceiver(new Consumer<Move>() { @Override public void consume(Move move) { 
			if (move == null) return; 
			play(move); 
		}});
		
		addMouseListener();
	    _refToAvoidGc2 = my(Timer.class).wakeUpEvery(150, new Scroller());
	    winImg=Toolkit.getDefaultToolkit().getImage(GoBoardPanel.class.getResource("images/winImg.png"));
	    loseImg=Toolkit.getDefaultToolkit().getImage(GoBoardPanel.class.getResource("images/loseImg.png"));
		createGridBuffer();
		
	}
	
	private void createGridBuffer() {
		_bufferGrid = new BufferedImage((int)(BOARD_IMAGE_SIZE+CELL_SIZE), (int)(BOARD_IMAGE_SIZE+CELL_SIZE), 
			      BufferedImage.TYPE_INT_ARGB);
		Graphics2D buffer = (Graphics2D)_bufferGrid.getGraphics();
		paintGridSmall(buffer);
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
		
		buffer.setColor(new Color(0,0,0,0));
		buffer.fillRect(0, 0, SCREEN_SIZE, SCREEN_SIZE);
			
		
		buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		buffer.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		buffer.setColor(Color.black);
		buffer.drawImage(_bufferGrid, 0, 0, this);
		//paintGridSmall(buffer);		
		drawHoverStone(buffer);
		paintStones(buffer);

		
				
		graphics.setColor(new Color(228,205,152));
		graphics.fillRect(0, 0, SCREEN_SIZE+10, SCREEN_SIZE+10);
		graphics.setColor(Color.black);
		((Graphics2D) graphics).draw(new Rectangle2D.Float(MARGIN+1, MARGIN+1, BOARD_IMAGE_SIZE-2, BOARD_IMAGE_SIZE-2));
		
		
		for (int i=0; i<9; i++) {
			int x=(int)(MARGIN+(BOARD_IMAGE_SIZE+CELL_SIZE)*((i % 3)-1));
			int y=(int)(MARGIN+(BOARD_IMAGE_SIZE+CELL_SIZE)*(Math.floor(i/3)-1));
			graphics.drawImage(_bufferImage, x, y, this);
		}
		
		
		int scW=scoreWhite().currentValue(),
		scB=scoreBlack().currentValue();
		if (scW==scB) return;
		if (_side==StoneColor.WHITE) isWinner=(scW>scB);
		else isWinner=(scW<scB);
		if (_board.nextToPlay()==null) graphics.drawImage((isWinner ? winImg : loseImg), 175, 185, this);
	}

	private void drawHoverStone(Graphics2D graphics) {
		if (!_board.canPlayStone(unscrollX(_hoverX), unscrollY(_hoverY))) return;

		if(_board.nextToPlay() == StoneColor.BLACK) graphics.setColor(new Color(0, 0, 0, 50));
		else graphics.setColor(new Color(255, 255, 255, 90));
			
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		paintStoneOnCoordinates(graphics, toCoordinateSmall(_hoverX), toCoordinateSmall(_hoverY), false);
	}

	private void paintGridSmall(Graphics2D buffer) {
		float c = 0;
		for(int i = 0; i <= BOARD_SIZE; i++ ) {
			buffer.setColor(Color.black);
			buffer.draw(new Line2D.Float(c, 0, c, BOARD_IMAGE_SIZE+CELL_SIZE));
			buffer.draw(new Line2D.Float(0, c, BOARD_IMAGE_SIZE+CELL_SIZE, c));
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
		
		float cx = toCoordinateSmall(scrollX(x));		
		float cy = toCoordinateSmall(scrollY(y));		
	
		graphics.setColor(toAwtColor(color));
		
		paintStoneOnCoordinates(graphics, cx, cy, dead);// ,(color==StoneColor.BLACK));
	}

	private Color toAwtColor(StoneColor color) {
		return color == StoneColor.BLACK? Color.black: Color.white;
	}

	private int scrollX(int x) { return (x + _scrollX) % BOARD_SIZE; }
	private int unscrollX(int x) { return (BOARD_SIZE + x - _scrollX) % BOARD_SIZE; }

	private int scrollY(int y) { return (y + _scrollY) % BOARD_SIZE; }
	private int unscrollY(int y) { return (BOARD_SIZE + y - _scrollY) % BOARD_SIZE; }
	
	private Graphics2D getBuffer() {
		_bufferImage = new BufferedImage((int)(BOARD_IMAGE_SIZE+CELL_SIZE), (int)(BOARD_IMAGE_SIZE+CELL_SIZE), 
			      BufferedImage.TYPE_INT_ARGB);
		return (Graphics2D)_bufferImage.getGraphics();
	}

	private float toCoordinateSmall(int position) {
		return position * CELL_SIZE;
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
		
		graphics.fill(new Ellipse2D.Float(x - (d / 2), y - (d / 2), d, d));
		//wrapping
		int buffersize=(int)(BOARD_IMAGE_SIZE+CELL_SIZE);
		
		if (x==0) graphics.fill(new Ellipse2D.Float(buffersize - (d / 2), y - (d / 2), d, d));
		if (y==0) graphics.fill(new Ellipse2D.Float(x - (d / 2), buffersize - (d / 2), d, d));
		if (x==buffersize) graphics.fill(new Ellipse2D.Float(- (d / 2), y - (d / 2), d, d)); 
		if (y==buffersize) graphics.fill(new Ellipse2D.Float(x - (d / 2), - (d / 2), d, d)); 
		
		if (x==0 & y==0) graphics.fill(new Ellipse2D.Float(buffersize - (d / 2), buffersize  - (d / 2), d, d));
		if (x==buffersize & y==0) graphics.fill(new Ellipse2D.Float(- (d / 2), buffersize  - (d / 2), d, d));
		if (x==buffersize & y==buffersize) graphics.fill(new Ellipse2D.Float(- (d / 2), - (d / 2), d, d));
		if (x==0 & y==buffersize) graphics.fill(new Ellipse2D.Float(buffersize- (d / 2), - (d / 2), d, d));
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

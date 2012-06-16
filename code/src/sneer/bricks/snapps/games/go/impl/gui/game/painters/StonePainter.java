package sneer.bricks.snapps.games.go.impl.gui.game.painters;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class StonePainter {

	private float _boardImageSize;
	private PiecePainter _piecePainter = new PiecePainter(); 
	private BufferedImage _whiteStoneAlive;
	private BufferedImage _blackStoneAlive;
	private BufferedImage _whiteStoneHover;
	private BufferedImage _blackStoneHover;
	private BufferedImage _whiteStoneDead;
	private BufferedImage _blackStoneDead;
	private RescaleOp _makeTransparentFiler;
	
	public StonePainter(float boardImageSize, float cellSize) {
		float[] transparentScales = { 1f, 1f, 1f, 0.5f };
		float[] transparentOffsets = new float[4];
		_makeTransparentFiler = new RescaleOp(transparentScales, transparentOffsets, null);
		
		setBoardDimensions(boardImageSize, cellSize);
	}

	public void setBoardDimensions(final float boardImageSize, final float cellSize){
		_boardImageSize = boardImageSize;
		int stoneDiameter = (int) (cellSize *0.97f);
		int deadStoneDiameter = (int) (cellSize *0.5f);
		
		_whiteStoneAlive = new BufferedImage(stoneDiameter, stoneDiameter, BufferedImage.TYPE_INT_ARGB);
		_piecePainter.paintPiece(_whiteStoneAlive, false);
		_whiteStoneHover = new BufferedImage(stoneDiameter, stoneDiameter, BufferedImage.TYPE_INT_ARGB);
		_makeTransparentFiler.filter(_whiteStoneAlive, _whiteStoneHover);
		_whiteStoneDead = new BufferedImage(deadStoneDiameter, deadStoneDiameter, BufferedImage.TYPE_INT_ARGB);
		_piecePainter.paintPiece(_whiteStoneDead, false);
		
		_blackStoneAlive = new BufferedImage(stoneDiameter, stoneDiameter, BufferedImage.TYPE_INT_ARGB);
		_piecePainter.paintPiece(_blackStoneAlive, true);
		_blackStoneHover = new BufferedImage(stoneDiameter, stoneDiameter, BufferedImage.TYPE_INT_ARGB);
		_makeTransparentFiler.filter(_blackStoneAlive, _blackStoneHover);
		_blackStoneDead = new BufferedImage(deadStoneDiameter, deadStoneDiameter, BufferedImage.TYPE_INT_ARGB);
		_piecePainter.paintPiece(_blackStoneDead, true);
	}
	
	public void paintStoneOnCoordinates(Graphics2D graphics, int x, int y,boolean black,boolean hover, boolean dead) {
		
		BufferedImage pieceToDraw = getPieceImageToDraw(black, hover, dead);
		
		int stoneDiameter = pieceToDraw.getWidth();
		int stoneRadius = stoneDiameter  / 2;
		int offsetX = x - stoneRadius;
		int offsetY = y - stoneRadius;
		graphics.drawImage(pieceToDraw, offsetX, offsetY, null);
				
		int boardSize=(int)(_boardImageSize);
		
		if (x==0){
			graphics.drawImage(pieceToDraw, 
					boardSize-stoneRadius , offsetY, boardSize, offsetY+stoneDiameter,
					0, 0, stoneRadius, stoneDiameter,
					null);
		}
		
		if (y==0){
			graphics.drawImage(pieceToDraw, 
					offsetX, boardSize-stoneRadius, offsetX+stoneDiameter, boardSize,
					0, 0, stoneDiameter, stoneRadius,
					null);
		}
		
		if (x==0 & y==0){
			graphics.drawImage(pieceToDraw, 
					boardSize-stoneRadius, boardSize-stoneRadius, boardSize, boardSize,
					0, 0, stoneRadius, stoneRadius,
					null);
		}
	}

	private BufferedImage getPieceImageToDraw(boolean black, boolean hover,
			boolean dead) {
		BufferedImage pieceToDraw = _blackStoneAlive;
		
		if(black) {			
			pieceToDraw = _blackStoneAlive;
		}
		if(black && hover) {			
			pieceToDraw = _blackStoneHover;
		}
		if(black && dead) {			
			pieceToDraw = _blackStoneDead;
		}
		final boolean white = !black;
		if(white) {			
			pieceToDraw = _whiteStoneAlive;
		}
		if(white && hover) {			
			pieceToDraw = _whiteStoneHover;
		}
		if(white && dead) {			
			pieceToDraw = _whiteStoneDead;
		}
		return pieceToDraw;
	}
}

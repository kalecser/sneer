package sneer.bricks.snapps.games.go.impl.gui.game.painters;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class StonePainter {

	private float _boardImageSize;
	private float _stoneDiameter;
	private PiecePainter _piecePainter = new PiecePainter(); 
	private BufferedImage _whiteStoneBig;
	private BufferedImage _blackStoneBig;
	private BufferedImage _whiteStoneSmall;
	private BufferedImage _blackStoneSmall;
	
	public StonePainter(float boardImageSize, float cellSize) {
		setBoardDimensions(boardImageSize, cellSize); 
	}

	public void setBoardDimensions(final float boardImageSize, final float cellSize){
		_boardImageSize = boardImageSize;
		_stoneDiameter = cellSize *0.97f;
		
		_whiteStoneBig = new BufferedImage((int)_stoneDiameter, (int)_stoneDiameter, BufferedImage.TYPE_INT_ARGB);
		_piecePainter.paintPiece(_whiteStoneBig, false);
		
		_blackStoneBig = new BufferedImage((int)_stoneDiameter, (int)_stoneDiameter, BufferedImage.TYPE_INT_ARGB);
		_piecePainter.paintPiece(_blackStoneBig, true);
	}
	
	public void paintStoneOnCoordinates(Graphics2D graphics, float x, float y,boolean black,boolean transparent, boolean dead) {
		
		int transparency = 255;
		if(transparent){
			transparency = 100;
		}
		
		BufferedImage pieceToDraw;
		
		if(black) {
			pieceToDraw= _blackStoneBig;
		}else{
			pieceToDraw= _whiteStoneBig;
		}
		
		float d = _stoneDiameter;
		if (dead) d*=0.6;
		
		int stoneRadius = (int) (d / 2);
		int offsetX = (int) (x - stoneRadius);
		int offsetY = (int) (y - stoneRadius);
		graphics.drawImage(pieceToDraw, offsetX, offsetY, null);
				
		int boardSize=(int)(_boardImageSize);
		
		int diameter = (int)_stoneDiameter;
		if (x==0)
			graphics.drawImage(pieceToDraw, 
					boardSize-stoneRadius , offsetY, boardSize, offsetY+diameter,
					0, 0, stoneRadius, diameter,
					null);
		
		if (x==boardSize)
			graphics.drawImage(pieceToDraw, 
					0, offsetY, stoneRadius, offsetY+diameter,
					stoneRadius, 0, diameter, diameter,
					null);
		
		if (y==0)
			graphics.drawImage(pieceToDraw, 
					offsetX, boardSize-stoneRadius, offsetX+diameter, boardSize,
					0, 0, diameter, stoneRadius,
					null); 
		
		if (x==0 & y==0)
			graphics.drawImage(pieceToDraw, 
					boardSize-stoneRadius, boardSize-stoneRadius, boardSize, boardSize,
					0, 0, stoneRadius, stoneRadius,
					null); 
	}
}

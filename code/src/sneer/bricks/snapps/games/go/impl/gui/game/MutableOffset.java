package sneer.bricks.snapps.games.go.impl.gui.game;

public class MutableOffset implements Offset{

	private int _xOffset;
	private int _yOffset;
	private float _boardImageSize = 0;
	
	@Override
	public void setBoardImageSize(float boardImageSize){
		_boardImageSize = boardImageSize;
	}
	
	@Override
	public int getXOffset(){
		return _xOffset;
	}
	
	@Override
	public int getYOffset(){
		return _yOffset;
	}
	
	@Override
	public void setXOffset(final float newXOffset){
		_xOffset = (int) (newXOffset % _boardImageSize);
	}
	
	@Override
	public void setYOffset(final float newYOffset){
		_yOffset = (int) (newYOffset % _boardImageSize);
	}
	
	@Override
	public void increaseXOffset(final float xIncrease){
		setXOffset(_xOffset +( xIncrease - _boardImageSize));
	}

	@Override
	public void increaseYOffset(final float yIncrease){
		setYOffset(_yOffset +(yIncrease - _boardImageSize));
	}
}

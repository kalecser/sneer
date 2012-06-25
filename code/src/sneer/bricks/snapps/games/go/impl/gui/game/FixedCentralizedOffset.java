package sneer.bricks.snapps.games.go.impl.gui.game;


public class FixedCentralizedOffset implements Offset {

	private int _yOffset;
	private int _xOffset;

	@Override
	public void increaseYOffset(float yIncrease) {
		//ignore
	}

	@Override
	public void increaseXOffset(float xIncrease) {
		//ignore
	}

	@Override
	public void setYOffset(float newYOffset) {
		//ignore
	}

	@Override
	public void setXOffset(float newXOffset) {
		//ignore
	}

	@Override
	public int getYOffset() {
		return _yOffset;
	}

	@Override
	public int getXOffset() {
		return _xOffset;
	}

	@Override
	public void setBoardImageSize(float boardImageSize) {
		_xOffset = (int) (boardImageSize/2);
		_yOffset = (int) (boardImageSize/2);
	}

}

package sneer.bricks.snapps.games.go.impl.gui.game;


public interface Offset {

	public void increaseYOffset(final float yIncrease);

	public void increaseXOffset(final float xIncrease);

	public void setYOffset(final float newYOffset);

	public void setXOffset(final float newXOffset);

	public int getYOffset();

	public int getXOffset();

	public void setBoardImageSize(float boardImageSize);

}

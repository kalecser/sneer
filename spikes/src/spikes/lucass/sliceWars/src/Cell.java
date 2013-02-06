package spikes.lucass.sliceWars.src;


public class Cell {

	public static final int EMPTY = 0;
	public static final int PLAYER1 = 1;
	public static final int PLAYER2 = 2;
	public int diceCount = 0;
	public int owner = EMPTY;

	public void connectTo(Cell other, int position) {
	}

}

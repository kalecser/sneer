package spikes.lucass.sliceWars.src.logic;


public class Cell {
	private static final int MAX_DICE = 6;
	private int diceCount = 0;
	public Player owner = Player.Empty;
	public int getDiceCount() {
		return diceCount;
	}
	
	public boolean canAddDie(){
		return getDiceCount()+1<=MAX_DICE;
	}
	
	public void addDie() {
		setDiceCount(getDiceCount() + 1);
		if(getDiceCount()>MAX_DICE)setDiceCount(MAX_DICE);
	}

	public void setDiceCount(int newDiceCount) {
		diceCount = newDiceCount;
	}

}

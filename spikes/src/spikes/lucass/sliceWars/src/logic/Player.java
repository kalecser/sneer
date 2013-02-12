package spikes.lucass.sliceWars.src.logic;

public enum Player{
	Empty, Player1, Player2;
	
	public boolean isLastPlayer(){
		if(this.equals(Player2)) return true;
		return false;
	}
	
	public Player next(){
		if(this.equals(Player1)) return Player2;
		if(this.equals(Player2)) return Player1;
		return null;
	}
}
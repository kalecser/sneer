package spikes.lucass.sliceWars.src.gameStates;


public interface GameState {

	public GameState play(int x, int y);
	public String getPhaseName();
	public String getWhoIsPlaying();

}
package spikes.lucass.sliceWars.src.gameStates;

import spikes.lucass.sliceWars.src.logic.Player;


public interface GameState {

	public GameState play(int x, int y);
	public String getPhaseName();
	public Player getWhoIsPlaying();
	public boolean canPass();
	public GameState pass();
}
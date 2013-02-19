package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContext.Phase;


public interface GameState {	
	
	public GameState play(int x, int y);
	public String getPhaseName();
	public Player getWhoIsPlaying();
	public boolean canPass();
	public GameState pass();
	public Phase getPhase();
}
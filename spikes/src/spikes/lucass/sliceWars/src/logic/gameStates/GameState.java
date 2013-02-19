package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.Player;


public interface GameState {

	public enum Phase{
		FILL_ALL_CELLS, FIRST_DICE_DISTRIBUTION, FIRST_ATTACKS, DICE_DISTRIBUTION, ATTACK, GAME_ENDED
	};	
	
	public GameState play(int x, int y);
	public String getPhaseName();
	public Player getWhoIsPlaying();
	public boolean canPass();
	public GameState pass();
	public abstract GameState.Phase getPhase();
}
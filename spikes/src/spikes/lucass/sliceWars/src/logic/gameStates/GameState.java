package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.PlayOutcome;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContextImpl.Phase;


public interface GameState {	
	
	public PlayOutcome play(int x, int y, GameStateContext gameStateContext);
	public String getPhaseName();
	public Player getWhoIsPlaying();
	public boolean canPass();
	public PlayOutcome pass(GameStateContext gameStateContext);
	public Phase getPhase();
}
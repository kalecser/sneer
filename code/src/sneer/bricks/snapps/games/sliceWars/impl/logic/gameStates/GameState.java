package sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates;

import sneer.bricks.snapps.games.sliceWars.impl.logic.PlayOutcome;
import sneer.bricks.snapps.games.sliceWars.impl.logic.Player;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.GameStateContextImpl.Phase;

public interface GameState {	
	
	public PlayOutcome play(int x, int y, GameStateContext gameStateContext);
	public String getPhaseName();
	public Player getWhoIsPlaying();
	public boolean canPass();
	public PlayOutcome pass(GameStateContext gameStateContext);
	public Phase getPhase();
}
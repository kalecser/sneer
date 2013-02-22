package spikes.lucass.sliceWars.test.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.PlayOutcome;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.GameState;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContext;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContextImpl.Phase;

public class GameStateMockAdapter implements GameState {

	@Override
	public PlayOutcome play(int x, int y, GameStateContext gameStateContext) {
		return null;
	}
	
	@Override
	public String getPhaseName() {
		return null;
	}

	@Override
	public Player getWhoIsPlaying() {
		return null;
	}

	@Override
	public boolean canPass() {
		return false;
	}

	@Override
	public PlayOutcome pass(GameStateContext gameStateContext) {
		return null;
	}

	@Override
	public Phase getPhase() {
		return null;
	}


}

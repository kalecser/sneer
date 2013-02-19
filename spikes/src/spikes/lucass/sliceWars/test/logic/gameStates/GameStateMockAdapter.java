package spikes.lucass.sliceWars.test.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.GameState;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContext.Phase;

public class GameStateMockAdapter implements GameState {

	@Override
	public GameState play(int x, int y) {
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
	public GameState pass() {
		return null;
	}

	@Override
	public Phase getPhase() {
		return null;
	}

}

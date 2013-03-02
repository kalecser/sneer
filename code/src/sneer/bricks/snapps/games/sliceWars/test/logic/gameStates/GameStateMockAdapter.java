package sneer.bricks.snapps.games.sliceWars.test.logic.gameStates;

import sneer.bricks.snapps.games.sliceWars.impl.logic.PlayOutcome;
import sneer.bricks.snapps.games.sliceWars.impl.logic.Player;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.GameState;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.GameStateContext;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.GameStateContextImpl.Phase;

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

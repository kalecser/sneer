package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.AttackOutcome;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContext.Phase;


public class ShowDiceOutcome implements GameState {

	private GameState _nextGameState;
	private AttackOutcome _attackOutcome;

	public ShowDiceOutcome(GameState nextGameState, AttackOutcome attackOutcome) {
		_nextGameState = nextGameState;
		_attackOutcome = attackOutcome;
	}

	@Override
	public GameState play(int x, int y) {
		return _nextGameState;
	}

	@Override
	public String getPhaseName() {
		return "Dice outcome";
	}

	@Override
	public Player getWhoIsPlaying() {
		return _nextGameState.getWhoIsPlaying();
	}

	@Override
	public boolean canPass() {
		return true;
	}

	@Override
	public GameState pass() {
		return _nextGameState;
	}

	@Override
	public Phase getPhase() {
		return Phase.ATTACK_OUTCOME;
	}

	public AttackOutcome getAttackOutcome() {
		return _attackOutcome;
	}

}

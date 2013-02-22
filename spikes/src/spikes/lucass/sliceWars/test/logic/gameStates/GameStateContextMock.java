package spikes.lucass.sliceWars.test.logic.gameStates;

import java.util.Collection;

import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.AttackCallback;
import spikes.lucass.sliceWars.src.logic.gameStates.DiceLeftCallback;
import spikes.lucass.sliceWars.src.logic.gameStates.GameState;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContext;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContextImpl.Phase;
import spikes.lucass.sliceWars.src.logic.gameStates.PlayCallback;

public class GameStateContextMock implements GameStateContext {

	protected GameState _state;

	@Override
	public void setState(GameState state) {
		_state = state;
	}

	@Override
	public Phase getPhase() {
		return _state.getPhase();
	}

	@Override
	public Player getWhoIsPlaying() {
		return _state.getWhoIsPlaying();
	}

	@Override
	public String getPhaseName() {
		return _state.getPhaseName();
	}

	@Override
	public void setAttackCallback(AttackCallback attackCallback) {
	}

	@Override
	public Collection<BoardCell> getBoardCells() {
		return null;
	}

	@Override
	public void pass() {
	}

	@Override
	public boolean canPass() {
		return false;
	}

	@Override
	public void play(int x, int y) {
	}

	@Override
	public void setDiceLeftCallback(DiceLeftCallback diceLeftCallback) {
	}

	@Override
	public void setPlayCallback(PlayCallback playCallback) {
	}

}

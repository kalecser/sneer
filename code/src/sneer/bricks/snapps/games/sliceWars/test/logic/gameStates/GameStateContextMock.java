package sneer.bricks.snapps.games.sliceWars.test.logic.gameStates;

import java.util.Collection;

import sneer.bricks.snapps.games.sliceWars.impl.logic.BoardCell;
import sneer.bricks.snapps.games.sliceWars.impl.logic.Player;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.AttackCallback;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.DiceLeftCallback;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.GameState;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.GameStateContext;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.GameStateContextImpl.Phase;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.PlayListener;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.SelectedCallback;

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
	public void addPlayListener(PlayListener playCallback) {
	}

	@Override
	public void setSelectedCellCallback(SelectedCallback selectedCellCall) {
	}

}

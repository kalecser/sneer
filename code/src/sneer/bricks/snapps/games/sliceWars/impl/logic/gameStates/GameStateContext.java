package sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates;

import java.util.Collection;

import sneer.bricks.snapps.games.sliceWars.impl.logic.BoardCell;
import sneer.bricks.snapps.games.sliceWars.impl.logic.Player;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.GameStateContextImpl.Phase;

public interface GameStateContext {
	public abstract void setState(GameState state);
	public abstract Phase getPhase();
	public abstract Player getWhoIsPlaying();
	public abstract String getPhaseName();
	public abstract void addPlayListener(PlayListener playListener);
	public abstract void setAttackCallback(AttackCallback attackCallback);
	public abstract void setDiceLeftCallback(DiceLeftCallback diceLeftCallback);
	public abstract void setSelectedCellCallback(SelectedCallback selectedCellCall);
	public abstract Collection<BoardCell> getBoardCells();
	public abstract void pass();
	public abstract boolean canPass();
	public abstract void play(int x, int y);
}
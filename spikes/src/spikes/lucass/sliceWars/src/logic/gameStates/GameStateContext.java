package spikes.lucass.sliceWars.src.logic.gameStates;

import java.util.Collection;

import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContextImpl.Phase;

public interface GameStateContext {
	public abstract void setState(GameState state);
	public abstract Phase getPhase();
	public abstract Player getWhoIsPlaying();
	public abstract String getPhaseName();
	public abstract void setAttackCallback(AttackCallback attackCallback);
	public abstract Collection<BoardCell> getBoardCells();
	public abstract void pass();
	public abstract boolean canPass();
	public abstract void play(int x, int y);
}
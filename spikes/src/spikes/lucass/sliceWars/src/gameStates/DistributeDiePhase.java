package spikes.lucass.sliceWars.src.gameStates;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.Player;


public class DistributeDiePhase implements GameState{

	public DistributeDiePhase(Board board, Player currentPlayer) {
	}

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

}

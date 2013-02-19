package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.Player;

public class GameEnded implements GameState {

	private Player _winner;

	public GameEnded(Player winner) {
		_winner = winner;
	}
	
	@Override
	public GameState play(int x, int y) {
		return this;
	}

	@Override
	public String getPhaseName() {
		return "Player "+_winner.getPlayerNumber()+" won";
	}

	@Override
	public Player getWhoIsPlaying() {
		return _winner;
	}

	@Override
	public boolean canPass() {
		return false;
	}

	@Override
	public GameState pass() {
		return this;
	}

	@Override
	public GameState.Phase getPhase(){
		return GameState.Phase.GAME_ENDED;
	}

}

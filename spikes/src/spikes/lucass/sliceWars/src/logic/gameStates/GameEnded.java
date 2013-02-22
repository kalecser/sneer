package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.PlayOutcome;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContextImpl.Phase;

public class GameEnded implements GameState {

	private Player _winner;

	public GameEnded(Player winner) {
		_winner = winner;
	}
	
	@Override
	public PlayOutcome play(int x, int y, GameStateContext gameStateContext){
		return null;
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
	public PlayOutcome pass(GameStateContext gameStateContext){
		return null;
	}

	@Override
	public Phase getPhase(){
		return Phase.GAME_ENDED;
	}

}

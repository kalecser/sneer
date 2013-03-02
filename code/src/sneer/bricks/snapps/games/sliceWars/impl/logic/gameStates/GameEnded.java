package sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates;

import sneer.bricks.snapps.games.sliceWars.impl.logic.PlayOutcome;
import sneer.bricks.snapps.games.sliceWars.impl.logic.Player;
import sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates.GameStateContextImpl.Phase;

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

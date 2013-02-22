package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.PlayOutcome;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContextImpl.Phase;


public class FirstAttacks implements GameState {

	private Board _board;
	private Attack _attackPhase;

	public FirstAttacks(Player currentPlayer, Board board) {
		_board = board;
		_attackPhase = new Attack(currentPlayer,board);
	}

	@Override
	public PlayOutcome play(int x, int y, GameStateContext gameStateContext){
		PlayOutcome playOutcome = _attackPhase.play(x, y, gameStateContext);
		gameStateContext.setState(this);
		return playOutcome;
	}

	@Override
	public String getPhaseName() {
		return "Attack phase";
	}

	@Override
	public Player getWhoIsPlaying() {
		return _attackPhase.getWhoIsPlaying();
	}

	@Override
	public boolean canPass() {
		return true;
	}

	@Override
	public PlayOutcome pass(GameStateContext gameStateContext){
		if(_attackPhase.getWhoIsPlaying().isLastPlayer()){
			gameStateContext.setState(new DiceDistribution(_attackPhase.getWhoIsPlaying().next(),_board));
			return null;
		}
		_attackPhase = new Attack(_attackPhase.getWhoIsPlaying().next(),_board);
		return null;
	}

	@Override
	public Phase getPhase(){
		return Phase.FIRST_ATTACKS;
	}
}

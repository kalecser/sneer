package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.Player;


public class FirstAttackPhase implements GameState {

	private Board _board;
	private AttackPhase _attackPhase;

	public FirstAttackPhase(Player currentPlayer, Board board) {
		_board = board;
		_attackPhase = new AttackPhase(currentPlayer,board);
	}

	@Override
	public GameState play(int x, int y) {
		_attackPhase.play(x, y);
		return this;
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
	public GameState pass() {
		if(_attackPhase.getWhoIsPlaying().isLastPlayer()){
			return new DistributeDiePhase(_attackPhase.getWhoIsPlaying().next(),_board);
		}
		_attackPhase = new AttackPhase(_attackPhase.getWhoIsPlaying().next(),_board);
		return this;
	}

	@Override
	public GameState.Phase getPhase(){
		return GameState.Phase.FIRST_ATTACK;
	}
}

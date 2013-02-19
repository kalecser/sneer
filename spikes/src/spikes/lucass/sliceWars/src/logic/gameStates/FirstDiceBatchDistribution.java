package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.Player;


public class FirstDiceBatchDistribution implements GameState {

	private Board _board;
	private final  int _diceToAdd;
	private Player _currentPlaying;
	private DistributeDiePhase _distributeDiePhase;

	public FirstDiceBatchDistribution(Player currentPlaying, Board board) {
		_currentPlaying = currentPlaying;
		_board = board;
		_diceToAdd = board.getCellCount()/currentPlaying.getPlayersCount();
		_distributeDiePhase = new DistributeDiePhase(currentPlaying, board, _diceToAdd);
	}

	@Override
	public GameState play(int x, int y) {
		GameState nextPhase = _distributeDiePhase.play(x, y);
		if(nextPhase.equals(_distributeDiePhase))
			return this;
		if(_currentPlaying.isLastPlayer()){
			return new FirstAttackPhase(_currentPlaying.next(), _board);
		}
		Player nextPlayer = _currentPlaying.next();
		_currentPlaying = nextPlayer;
		_distributeDiePhase = new DistributeDiePhase(nextPlayer, _board, _diceToAdd);
		return this;
	}
	
	@Override
	public String getPhaseName() {
		return "First round, add dice";
	}
	
	@Override
	public Player getWhoIsPlaying() {
		return _currentPlaying;
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
		return GameState.Phase.FIRST_DICE_DISTRIBUTION;
	}
}

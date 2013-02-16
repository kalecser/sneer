package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.Player;


public class FirstDiceBatchDistribution implements GameState {

	private Board _board;
	private final  int _diceToAdd;
	private Player _currentPlaying;
	private int diceCount;
	
	public FirstDiceBatchDistribution(Player currentPlaying, Board board) {
		_currentPlaying = currentPlaying;
		_board = board;
		int playersCount = 2;
		_diceToAdd = board.getCellCount()/playersCount;
		diceCount = _diceToAdd;
	}

	@Override
	public GameState play(int x, int y) {
		BoardCell cellAtOrNull = _board.getCellAtOrNull(x,y);
		if(cellAtOrNull == null) return this;
		if(!cellAtOrNull.getOwner().equals(_currentPlaying)) return this;
		if(!cellAtOrNull.canAddDie()) return this;
		diceCount --;
		cellAtOrNull.addDie();
		if(diceCount == 0){
			if(_currentPlaying.isLastPlayer()){
				return new FirstAttackPhase(_currentPlaying, _board);
			}
			_currentPlaying = _currentPlaying.next();
			diceCount = _diceToAdd;
		}
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
}

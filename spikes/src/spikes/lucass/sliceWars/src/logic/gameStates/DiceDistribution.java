package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.PlayOutcome;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContextImpl.Phase;


public class DiceDistribution implements GameState{
	
	private final Board _board;
	private final  int _diceToAdd;
	private Player _currentPlaying;
	private int diceCount;
	
	public DiceDistribution(final Player currentPlaying,final Board board,final int diceToAdd) {
		_currentPlaying = currentPlaying;
		_board = board;
		_diceToAdd = diceToAdd;
		diceCount = _diceToAdd;
	}

	public DiceDistribution(final Player currentPlaying,final Board board) {
		this(currentPlaying,board,board.getBiggestLinkedCellCountForPlayer(currentPlaying));
	}

	@Override
	public PlayOutcome play(final int x, final int y, final GameStateContext gameStateContext){
		BoardCell cellAtOrNull = _board.getCellAtOrNull(x,y);
		if(cellAtOrNull == null) return null;
		if(!cellAtOrNull.getOwner().equals(_currentPlaying)) return null;
		if(_board.areaAllCellsFilledByPlayer(_currentPlaying)){
			gameStateContext.setState(new Attack(_currentPlaying, _board));
			return null;
		}
		if(!cellAtOrNull.canAddDie()) return null;
		diceCount --;
		PlayOutcome playOutcome = new PlayOutcome(diceCount);
		cellAtOrNull.addDie();
		if(diceCount == 0 || _board.areaAllCellsFilledByPlayer(_currentPlaying)){
			gameStateContext.setState(new Attack(_currentPlaying, _board));
		}
		return playOutcome;
	}

	@Override
	public String getPhaseName() {
		return "DistributeDiePhase";
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
	public PlayOutcome pass(final GameStateContext gameStateContext){
		return null;
	}

	@Override
	public Phase getPhase(){
		return Phase.DICE_DISTRIBUTION;
	}

	public int getDiceToAdd() {
		return _diceToAdd;
	}

}

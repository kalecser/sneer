package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.Player;


public class FillAllCell implements GameState {
	
	private Board _board;
	private Player _currentPlaying;

	public FillAllCell(Player currentPlaying,Board board) {
		_board = board;
		_currentPlaying = currentPlaying;
	}
	
	@Override
	public GameState play(int x, int y){
		BoardCell cellAtOrNull = _board.getCellAtOrNull(x,y);
		if(cellAtOrNull == null) return this;
		if(!cellAtOrNull.getOwner().equals(Player.EMPTY)) return this;
		
		cellAtOrNull.setOwner(_currentPlaying);
		cellAtOrNull.setDiceCount(1);
		_currentPlaying = _currentPlaying.next();
		
		if(!_board.isFilled()) return this;
		Player firstPlayer = new Player(1, _currentPlaying.getPlayersCount());
		return new FirstDiceDistribution(firstPlayer,_board);
	}

	@Override
	public String getPhaseName() {
		return "Fill all cells phase";
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
		return GameState.Phase.FILL_ALL_CELLS;
	}
}

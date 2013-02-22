package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.PlayOutcome;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContextImpl.Phase;


public class FillAllCell implements GameState {
	
	private Board _board;
	private Player _currentPlaying;

	public FillAllCell(Player currentPlaying,Board board) {
		_board = board;
		_currentPlaying = currentPlaying;
	}
	
	@Override
	public PlayOutcome play(int x, int y, GameStateContext gameStateContext){
		BoardCell cellAtOrNull = _board.getCellAtOrNull(x,y);
		if(cellAtOrNull == null) return null;
		if(!cellAtOrNull.getOwner().equals(Player.EMPTY)) return null;
		
		cellAtOrNull.setOwner(_currentPlaying);
		cellAtOrNull.setDiceCount(1);
		_currentPlaying = _currentPlaying.next();
		
		if(!_board.isFilled()) return null;
		Player firstPlayer = new Player(1, _currentPlaying.getPlayersCount());
		gameStateContext.setState(new FirstDiceDistribution(firstPlayer,_board));
		return null;
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
	public PlayOutcome pass(GameStateContext gameStateContext){
		return null;
	}
	
	@Override
	public Phase getPhase(){
		return Phase.FILL_ALL_CELLS;
	}
}

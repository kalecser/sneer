package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.Player;


public class FillAllCellPhase implements GameState {
	
	private Board _board;
	private Player currentPlaying = Player.Player1;

	public FillAllCellPhase(Board board) {
		_board = board;
	}
	
	@Override
	public GameState play(int x, int y){
		BoardCell cellAtOrNull = _board.getCellAtOrNull(x,y);
		if(cellAtOrNull == null) return this;
		if(!cellAtOrNull.getOwner().equals(Player.Empty)) return this;
		
		cellAtOrNull.setOwner(currentPlaying);
		cellAtOrNull.setDiceCount(1);
		currentPlaying = currentPlaying.next();
		
		if(!_board.isFilled()) return this;
		
		return new FirstDiceBatchDistribution(_board);
	}

	@Override
	public String getPhaseName() {
		return "Fill all cells phase";
	}

	@Override
	public Player getWhoIsPlaying() {
		return currentPlaying;
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

package spikes.lucass.sliceWars.src.logic.gameStates;

import java.util.Set;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.Player;


public class FirstDiceBatchDistribution implements GameState {

	private Board _board;
	private final  int _diceToAdd;
	private Player currentPlaying = Player.Player1;
	private int diceCount;
	
	public FirstDiceBatchDistribution(Board board) {
		_board = board;
		Set<BoardCell> boardCells = _board.getBoardCells();
		_diceToAdd = boardCells.size()/2;
		diceCount = _diceToAdd;
	}

	@Override
	public GameState play(int x, int y) {
		BoardCell cellAtOrNull = _board.getCellAtOrNull(x,y);
		if(cellAtOrNull == null) return this;
		if(!cellAtOrNull.cell.owner.equals(currentPlaying)) return this;
		if(!cellAtOrNull.cell.canAddDie()) return this;
		diceCount --;
		cellAtOrNull.cell.addDie();
		if(diceCount == 0){
			if(currentPlaying.isLastPlayer()){
				return new FirstAttackPhase(_board);
			}
			currentPlaying = currentPlaying.next();
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

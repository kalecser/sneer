package spikes.lucass.sliceWars.src.gameStates;

import java.util.Set;

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
		if(!cellAtOrNull.cell.owner.equals(Player.Empty)) return this;
		cellAtOrNull.cell.owner = currentPlaying;
		cellAtOrNull.cell.setDiceCount(1);
		if(currentPlaying.equals(Player.Player1))
			currentPlaying = Player.Player2;
		else
			currentPlaying = Player.Player1;
		
		Set<BoardCell> boardCells = _board.getBoardCells();
		for (BoardCell boardCell : boardCells) {
			if(boardCell.cell.owner.equals(Player.Empty))
				return this;
		}
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
	public void pass() {
	}
}

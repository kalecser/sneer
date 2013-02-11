package spikes.lucass.sliceWars.src.gameStates;

import spikes.lucass.sliceWars.src.Board;
import spikes.lucass.sliceWars.src.BoardCell;
import spikes.lucass.sliceWars.src.Player;


public class FillAllCellPhase {
	
	private Board _board;
	private Player currentPlaying = Player.Player1;

	public FillAllCellPhase(Board board) {
		_board = board;
	}
	
	public void play(int x, int y){
		BoardCell cellAtOrNull = _board.getCellAtOrNull(x,y);
		if(cellAtOrNull == null) return;
		if(!cellAtOrNull.cell.owner.equals(Player.Empty)) return;
		cellAtOrNull.cell.owner = currentPlaying;
		if(currentPlaying.equals(Player.Player1))
			currentPlaying = Player.Player2;
		else
			currentPlaying = Player.Player1;
	}

}

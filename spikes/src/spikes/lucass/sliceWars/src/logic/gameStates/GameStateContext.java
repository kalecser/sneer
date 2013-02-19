package spikes.lucass.sliceWars.src.logic.gameStates;

import java.util.Collection;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.Player;


public class GameStateContext {
	
	private GameState _state;
	private Board _board;

	public enum Phase{
		FILL_ALL_CELLS, FIRST_DICE_DISTRIBUTION, FIRST_ATTACKS, DICE_DISTRIBUTION, ATTACK, ATTACK_OUTCOME, GAME_ENDED
	};
	
	public GameStateContext(int numberOfPlayers, Board board) {
		_board = board;
		_state = new FillAllCell(new Player(1, numberOfPlayers), board);
	}

	public void play(int x, int y){
		_state = _state.play(x, y);
	}
	
	public String getPhaseName(){
		return _state.getPhaseName();
	}
	
	public Player getWhoIsPlaying(){
		return _state.getWhoIsPlaying();
	}
	
	public boolean canPass(){
		return _state.canPass();
	}
	
	public void pass(){
		_state = _state.pass();
	}
	
	public Phase getPhase(){
		return _state.getPhase();
	}

	public Collection<BoardCell> getBoardCells() {
		return _board.getBoardCells();
	}

}

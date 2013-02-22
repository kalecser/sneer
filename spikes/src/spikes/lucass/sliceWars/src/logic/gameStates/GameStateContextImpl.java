package spikes.lucass.sliceWars.src.logic.gameStates;

import java.util.Collection;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.PlayOutcome;
import spikes.lucass.sliceWars.src.logic.Player;


public class GameStateContextImpl implements GameStateContext {
	
	private GameState _state;
	private Board _board;
	private AttackCallback _attackCallback;

	public enum Phase{
		FILL_ALL_CELLS, FIRST_DICE_DISTRIBUTION, FIRST_ATTACKS, DICE_DISTRIBUTION, ATTACK, ATTACK_OUTCOME, GAME_ENDED
	};
	
	public GameStateContextImpl(int numberOfPlayers, Board board) {
		this(board, new FillAllCell(new Player(1, numberOfPlayers), board));
	}

	public GameStateContextImpl(Board board,GameState gameState) {
		_board = board;
		setState(gameState);
	}

	@Override
	public void play(int x, int y){
		callCallbacks(_state.play(x, y,this));
	}
	
	@Override
	public String getPhaseName(){
		return _state.getPhaseName();
	}
	
	@Override
	public Player getWhoIsPlaying(){
		return _state.getWhoIsPlaying();
	}
	
	@Override
	public boolean canPass(){
		return _state.canPass();
	}
	
	@Override
	public void pass(){
		callCallbacks(_state.pass(this));
	}

	private void callCallbacks(PlayOutcome playOutcome) {
		if(playOutcome != null && playOutcome.isAttackOutcome())
			_attackCallback.attackedWithOutcome(playOutcome.getAttackOutcome());
	}
	
	@Override
	public Phase getPhase(){
		return _state.getPhase();
	}

	@Override
	public Collection<BoardCell> getBoardCells() {
		return _board.getBoardCells();
	}

	@Override
	public void setState(GameState state) {
		_state = state;
	}

	@Override
	public void setAttackCallback(AttackCallback attackCallback) {
		_attackCallback = attackCallback;
	}

}

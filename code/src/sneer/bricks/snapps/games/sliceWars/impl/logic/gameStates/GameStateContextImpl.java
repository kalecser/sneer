package sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sneer.bricks.snapps.games.sliceWars.impl.logic.Board;
import sneer.bricks.snapps.games.sliceWars.impl.logic.BoardCell;
import sneer.bricks.snapps.games.sliceWars.impl.logic.PlayOutcome;
import sneer.bricks.snapps.games.sliceWars.impl.logic.Player;

public class GameStateContextImpl implements GameStateContext {
	
	private GameState _state;
	private Board _board;
	private AttackCallback _attackCallback;
	private DiceLeftCallback _diceLeftCallback;
	private List<PlayListener> _playListeners = new ArrayList<PlayListener>();
	private SelectedCallback _selectedCellCall;

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
		callPlayListeners();
		callCallbacks(_state.play(x, y,this));
	}
	
	@Override
	public void pass(){
		callPlayListeners();
		callCallbacks(_state.pass(this));
	}

	private void callPlayListeners() {
		for (PlayListener playListener : _playListeners) {
			playListener.played();			
		}
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

	private void callCallbacks(PlayOutcome playOutcome) {
		if(playOutcome == null) return;
		if(_selectedCellCall != null)
			_selectedCellCall.selectedOrNull(playOutcome.getSelectedCellOrNull());
		if(_diceLeftCallback != null)
			_diceLeftCallback.diceLeft(playOutcome.getDiceLeft());
		if(playOutcome.isAttackOutcome())
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

	@Override
	public void setDiceLeftCallback(DiceLeftCallback diceLeftCallback) {
		_diceLeftCallback = diceLeftCallback;
	}

	@Override
	public void addPlayListener(PlayListener playListener) {
		_playListeners.add(playListener);
	}

	@Override
	public void setSelectedCellCallback(SelectedCallback selectedCellCall) {
		_selectedCellCall = selectedCellCall;
	}

}

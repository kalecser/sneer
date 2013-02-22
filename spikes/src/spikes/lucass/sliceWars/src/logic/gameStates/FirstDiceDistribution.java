package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.PlayOutcome;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContextImpl.Phase;


public class FirstDiceDistribution implements GameState {

	private Board _board;
	private final  int _diceToAdd;
	private Player _currentPlaying;
	private DiceDistribution _distributeDiePhase;

	public FirstDiceDistribution(Player currentPlaying, Board board) {
		_currentPlaying = currentPlaying;
		_board = board;
		_diceToAdd = board.getCellCount()/currentPlaying.getPlayersCount();
		_distributeDiePhase = new DiceDistribution(currentPlaying, board, _diceToAdd);
	}

	@Override
	public PlayOutcome play(int x, int y, GameStateContext gameStateContext){
		_distributeDiePhase.play(x, y, gameStateContext);
		if(gameStateContext.getPhase().equals(Phase.FIRST_DICE_DISTRIBUTION)){
			return null;
		}
		if(_currentPlaying.isLastPlayer()){
			gameStateContext.setState(new FirstAttacks(_currentPlaying.next(), _board));
			return null;
		}
			
		gameStateContext.setState(this);
		Player nextPlayer = _currentPlaying.next();
		_currentPlaying = nextPlayer;
		_distributeDiePhase = new DiceDistribution(nextPlayer, _board, _diceToAdd);
		return null;
	}
	
	@Override
	public String getPhaseName() {
		return "First round, add dice";
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
		return Phase.FIRST_DICE_DISTRIBUTION;
	}
}

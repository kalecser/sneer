package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.AttackOutcome;
import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.PlayOutcome;
import spikes.lucass.sliceWars.src.logic.Player;
import spikes.lucass.sliceWars.src.logic.gameStates.GameStateContextImpl.Phase;

public class Attack implements GameState {

	private final Board _board;
	private final Player currentPlaying;
	private BoardCell c1;
	

	public Attack(Player playerAttacking,Board board) {
		currentPlaying = playerAttacking;
		_board = board;
	}

	@Override
	public PlayOutcome play(int x, int y, GameStateContext gameStateContext){
		BoardCell cellAtOrNull = _board.getCellAtOrNull(x,y);
		if(cellAtOrNull == null) return null;
		if(c1 == null){
			Player owner = cellAtOrNull.getOwner();
			if(!owner.equals(currentPlaying)){
				return new PlayOutcome();
			}
			c1 = cellAtOrNull;
			PlayOutcome playOutcome = new PlayOutcome();
			playOutcome.selectedACell(c1);
			return playOutcome;
		}
		if(cellAtOrNull.getOwner().equals(currentPlaying)){
			c1 = cellAtOrNull;
			PlayOutcome playOutcome = new PlayOutcome();
			playOutcome.selectedACell(c1);
			return playOutcome;
		}
		if(!_board.areLinked(c1, cellAtOrNull)){
			c1 = null;
			return new PlayOutcome();
		}
		AttackOutcome attackOutcome = c1.attack(cellAtOrNull);
		if(attackOutcome == null){
			c1 = null;
			return new PlayOutcome();
		}
		c1.setCell(attackOutcome.attackCellAfterAttack);
		cellAtOrNull.setCell(attackOutcome.defenseCellAfterAttack);
		c1 = null;
		if(checkIfWon()){
			gameStateContext.setState(new GameEnded(currentPlaying));
		}
		return new PlayOutcome(attackOutcome);
	}

	@Override
	public String getPhaseName() {
		return "Attack phase";
	}

	@Override
	public Player getWhoIsPlaying() {
		return currentPlaying;
	}

	@Override
	public boolean canPass() {
		return true;
	}

	private boolean checkIfWon(){
		Player nextPlayer = currentPlaying.next();
		while(_board.getBiggestLinkedCellCountForPlayer(nextPlayer) == 0){
			nextPlayer = nextPlayer.next();
			if(nextPlayer.equals(currentPlaying)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public PlayOutcome pass(GameStateContext gameStateContext){
		Player nextPlayer = currentPlaying.next();
		if(_board.areaAllCellsFilledByPlayer(nextPlayer)){
			gameStateContext.setState(new Attack(nextPlayer, _board));
			return null;
		}
		while(_board.getBiggestLinkedCellCountForPlayer(nextPlayer) == 0){
			nextPlayer = nextPlayer.next();
			if(nextPlayer.equals(currentPlaying)){
				throw new RuntimeException("This shouldn't happen because if a play leads to a winner, the game state changes");
			}
		}
		DiceDistribution diceDistribution = new DiceDistribution(nextPlayer,_board);
		gameStateContext.setState(diceDistribution);
		return new PlayOutcome(diceDistribution.getDiceToAdd());
	}

	@Override
	public Phase getPhase(){
		return Phase.ATTACK;
	}

}

package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.AttackOutcome;
import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.Player;

public class AttackPhase implements GameState {

	private final Board _board;
	private final Player currentPlaying;
	private BoardCell c1;
	

	public AttackPhase(Player playerAttacking,Board board) {
		currentPlaying = playerAttacking;
		_board = board;
	}

	@Override
	public GameState play(int x, int y) {
		BoardCell cellAtOrNull = _board.getCellAtOrNull(x,y);
		if(cellAtOrNull == null) return this;
		if(c1 == null){
			Player owner = cellAtOrNull.getOwner();
			if(!owner.equals(currentPlaying)) return this;
			c1 = cellAtOrNull;
			return this;
		}
		if(!_board.areLinked(c1, cellAtOrNull)){
			c1 = null;
			return this;
		}
		AttackOutcome attackOutcome = c1.attack(cellAtOrNull);
		if(attackOutcome == null){
			c1 = null;
			return this;
		}
		c1.setCell(attackOutcome.attackCellAfterAttack);
		cellAtOrNull.setCell(attackOutcome.defenseCellAfterAttack);
		c1 = null;
		return this;
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

	@Override
	public GameState pass() {
		Player nextPlayer = currentPlaying.next();
		return new DistributeDiePhase(nextPlayer,_board);
	}

}

package spikes.lucass.sliceWars.src.gameStates;

import spikes.lucass.sliceWars.src.logic.AttackOutcome;
import spikes.lucass.sliceWars.src.logic.Board;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.CellAttack;
import spikes.lucass.sliceWars.src.logic.DiceImpl;
import spikes.lucass.sliceWars.src.logic.DiceThrowerImpl;
import spikes.lucass.sliceWars.src.logic.Player;


public class AttackPhase implements GameState {

	private Board _board;
	private Player currentPlaying = Player.Player1;
	private BoardCell c1;
	private CellAttack _cellAttack;

	public AttackPhase(Board board) {
		_board = board;
		_cellAttack = new CellAttack(new DiceThrowerImpl(new DiceImpl(), new DiceImpl()));
	}

	@Override
	public GameState play(int x, int y) {
		BoardCell cellAtOrNull = _board.getCellAtOrNull(x,y);
		if(cellAtOrNull == null) return this;
		if(c1 == null){
			c1 = cellAtOrNull;
			return this;
		}
		AttackOutcome doAttack = _cellAttack.doAttackReturnOutcomeOrNull(c1.cell, cellAtOrNull.cell);
		if(doAttack == null){
			c1 = null;
			return this;
		}
		c1.cell = doAttack.attackCellAfterAttack;
		cellAtOrNull.cell = doAttack.defenseCellAfterAttack;
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
	public void pass() {
		currentPlaying = currentPlaying.next();
	}

}

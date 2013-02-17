package spikes.lucass.sliceWars.test.logic.gameStates;

import java.awt.Polygon;

import spikes.lucass.sliceWars.src.logic.AttackOutcome;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.Cell;
import spikes.lucass.sliceWars.src.logic.DiceThrowOutcome;
import spikes.lucass.sliceWars.src.logic.Player;

public class BoardCellMock implements BoardCell {

	private boolean wasAttacked = false;
	private Player _player;
	private int diceCount = 0;
	
	public BoardCellMock(Player player) {
		_player = player;
	}

	@Override
	public Player getOwner() {
		return _player;
	}

	@Override
	public AttackOutcome attack(BoardCell other) {
		BoardCellMock otherMock = (BoardCellMock) other;
		otherMock.wasAttacked = true;
		return new AttackOutcome(other.getCell(), this.getCell(), new DiceThrowOutcome(new int[]{}, new int[]{}));
	}

	@Override
	public void setCell(Cell attackCellAfterAttack) {
	}

	@Override
	public Cell getCell() {
		return null;
	}

	@Override
	public void setOwner(Player player1) {
	}

	@Override
	public void setDiceCount(int newDiceCount) {
	}

	@Override
	public boolean canAddDie() {
		return true;
	}

	@Override
	public void addDie() {
		diceCount++;
	}

	@Override
	public Polygon getPolygon() {
		return null;
	}

	@Override
	public int getDiceCount() {
		return diceCount;
	}

	public boolean wasAttacked() {
		return wasAttacked;
	}

}

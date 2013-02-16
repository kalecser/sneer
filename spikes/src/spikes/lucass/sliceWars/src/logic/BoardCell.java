package spikes.lucass.sliceWars.src.logic;

import java.awt.Polygon;


public class BoardCell {
	
	public Polygon polygon;
	private Cell cell;
	private static CellAttack _cellAttack = new CellAttack(new DiceThrowerImpl(new DiceImpl(), new DiceImpl()));;
	
	public BoardCell(Polygon p) {
		polygon = p;
		setCell(new Cell());
	}

	public Player getOwner() {
		return cell.owner;
	}

	public void setCell(Cell newCell) {
		cell = newCell;
	}

	public void setOwner(Player newOwner) {
		cell.owner = newOwner;
	}

	public boolean canAddDie() {
		return cell.canAddDie();
	}

	public void addDie() {
		cell.addDie();
	}

	public AttackOutcome attack(BoardCell other) {
		return _cellAttack.doAttackReturnOutcomeOrNull(this.cell, other.cell);
	}

	public int getDiceCount() {
		return cell.getDiceCount();
	}

	public void setDiceCount(int newDiceCount) {
		cell.setDiceCount(newDiceCount);
	}

}

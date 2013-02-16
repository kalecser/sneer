package spikes.lucass.sliceWars.src.logic;

import java.awt.Polygon;


public class BoardCellImpl implements BoardCell {
	
	private Polygon polygon;
	private Cell cell;
	private static CellAttack _cellAttack = new CellAttack(new DiceThrowerImpl(new DiceImpl(), new DiceImpl()));;
	
	public BoardCellImpl(Polygon p) {
		polygon = p;
		setCell(new Cell());
	}

	@Override
	public Player getOwner() {
		return cell.owner;
	}

	@Override
	public void setCell(Cell newCell) {
		cell = newCell;
	}

	@Override
	public void setOwner(Player newOwner) {
		cell.owner = newOwner;
	}

	@Override
	public boolean canAddDie() {
		return cell.canAddDie();
	}

	@Override
	public void addDie() {
		cell.addDie();
	}

	@Override
	public AttackOutcome attack(BoardCell other) {
		return _cellAttack.doAttackReturnOutcomeOrNull(this.cell, other.getCell());
	}

	@Override
	public int getDiceCount() {
		return cell.getDiceCount();
	}

	@Override
	public void setDiceCount(int newDiceCount) {
		cell.setDiceCount(newDiceCount);
	}

	@Override
	public Cell getCell() {
		return cell;
	}

	@Override
	public Polygon getPolygon() {
		return polygon;
	}

}

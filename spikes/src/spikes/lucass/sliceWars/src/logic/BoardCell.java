package spikes.lucass.sliceWars.src.logic;

import java.awt.Polygon;

public interface BoardCell {

	public Player getOwner();
	public AttackOutcome attack(BoardCell other);
	public void setCell(Cell attackCellAfterAttack);
	public Cell getCell();
	public void setOwner(Player player1);
	public void setDiceCount(int newDiceCount);
	public boolean canAddDie();
	public void addDie();
	public Polygon getPolygon();
	public int getDiceCount();

}
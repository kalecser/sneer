package sneer.bricks.snapps.games.sliceWars.impl.logic;

public class PlayOutcome {

	private AttackOutcome _attackOutcome;
	private BoardCell _cell;
	private int _diceLeft;

	public PlayOutcome(AttackOutcome attackOutcome) {
		_attackOutcome = attackOutcome;
	}

	public PlayOutcome(int diceLeft) {
		_diceLeft = diceLeft;
	}

	public PlayOutcome() {
	}

	public boolean isAttackOutcome() {
		return _attackOutcome != null;
	}

	public AttackOutcome getAttackOutcome() {
		return _attackOutcome;
	}

	public void selectedACell(BoardCell cell) {
		_cell = cell;
	}

	public BoardCell getSelectedCellOrNull() {
		return _cell;
	}

	public int getDiceLeft() {
		return _diceLeft;
	}

}

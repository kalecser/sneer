package spikes.lucass.sliceWars.src.logic;

public class PlayOutcome {

	private AttackOutcome _attackOutcome;
	private BoardCell _cell;

	public PlayOutcome(AttackOutcome attackOutcome) {
		_attackOutcome = attackOutcome;
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

}

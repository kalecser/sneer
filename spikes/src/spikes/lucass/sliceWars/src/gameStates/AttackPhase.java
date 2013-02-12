package spikes.lucass.sliceWars.src.gameStates;

import spikes.lucass.sliceWars.src.logic.Board;


public class AttackPhase implements GameState {

	public AttackPhase(Board board) {
	}

	@Override
	public GameState play(int x, int y) {
		return this;
	}

}

package spikes.lucass.sliceWars.src.logic.gameStates;

import spikes.lucass.sliceWars.src.logic.AttackOutcome;

public interface AttackCallback {

	public void attackedWithOutcome(AttackOutcome attackOutcome);

}

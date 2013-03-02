package sneer.bricks.snapps.games.sliceWars.impl.logic.gameStates;

import sneer.bricks.snapps.games.sliceWars.impl.logic.AttackOutcome;

public interface AttackCallback {

	public void attackedWithOutcome(AttackOutcome attackOutcome);

}

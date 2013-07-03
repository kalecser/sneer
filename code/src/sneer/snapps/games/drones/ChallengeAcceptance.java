package sneer.snapps.games.drones;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;

public class ChallengeAcceptance extends Tuple {

	public final String hitpoints;
	public final String strength;
	public final String armor;

	public ChallengeAcceptance(Seal challenger, String hitpoints, String strength, String armor) {
		super(challenger);
		this.hitpoints = hitpoints;
		this.strength = strength;
		this.armor = armor;
	}

}

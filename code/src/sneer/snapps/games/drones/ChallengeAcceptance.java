package sneer.snapps.games.drones;

import sneer.bricks.expression.tuples.Tuple;

public class ChallengeAcceptance extends Tuple {

	public final String hitpoints;
	public final String strength;
	public final String armor;

	public ChallengeAcceptance(String hitpoints, String strength, String armor) {
		this.hitpoints = hitpoints;
		this.strength = strength;
		this.armor = armor;
	}

}

package sneer.bricks.snapps.games.sliceWars.impl.sneer;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;

public class SliceWarsInvitation extends Tuple {
	
	public final long seed;
	
	public SliceWarsInvitation(Seal addressee_, long _seed) {
		super(addressee_);
		seed = _seed;
	}
	
}

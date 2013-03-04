package sneer.bricks.snapps.games.sliceWars.impl.sneer;

import static basis.environments.Environments.my;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.snapps.games.sliceWars.impl.RemotePlay;
import sneer.bricks.snapps.games.sliceWars.impl.RemotePlayListener;

public class SneerPlayer implements RemotePlayListener {

	private final Seal _addressee;
	
	public SneerPlayer(Seal addressee) {
		_addressee = addressee;
	}
	
	@Override
	public void play(RemotePlay play) {
		SliceWarsPlay sliceWarsPlay = new SliceWarsPlay(_addressee, play);
		my(TupleSpace.class).add(sliceWarsPlay);
	}

}

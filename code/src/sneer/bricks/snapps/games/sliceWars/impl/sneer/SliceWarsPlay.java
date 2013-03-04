package sneer.bricks.snapps.games.sliceWars.impl.sneer;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.snapps.games.sliceWars.impl.RemotePlay;

public class SliceWarsPlay extends Tuple {
	
	public final RemotePlay remotePlay;
	
	public SliceWarsPlay(Seal addressee_, RemotePlay _remotePlay) {
		super(addressee_);
		remotePlay = _remotePlay;
	}
	
}

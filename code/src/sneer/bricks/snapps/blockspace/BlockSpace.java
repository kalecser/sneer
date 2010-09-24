package sneer.bricks.snapps.blockspace;

import sneer.bricks.identity.seals.Seal;
import sneer.foundation.brickness.Brick;

@Brick
public interface BlockSpace {

	Bucket localBucket();
	Bucket remoteBucketBy(Seal seal);
}

package sneer.bricks.hardwaresharing.dataspace;

import sneer.bricks.identity.seals.Seal;
import sneer.foundation.brickness.Brick;

@Brick
public interface DataSpace {

	Bucket localBucket();
	Bucket remoteBucketBy(Seal seal);
}

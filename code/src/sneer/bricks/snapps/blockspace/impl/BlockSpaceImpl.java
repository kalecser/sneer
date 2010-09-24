package sneer.bricks.snapps.blockspace.impl;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.snapps.blockspace.BlockSpace;
import sneer.bricks.snapps.blockspace.Bucket;

public class BlockSpaceImpl implements BlockSpace {

	@Override
	public Bucket localBucket() {
		return new BucketImpl();
		}

	@Override
	public Bucket remoteBucketBy(Seal seal) {
		return new RemoteBucket(seal);
	}

}

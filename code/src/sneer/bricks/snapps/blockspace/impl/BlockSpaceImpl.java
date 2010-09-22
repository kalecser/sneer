package sneer.bricks.snapps.blockspace.impl;

import sneer.bricks.snapps.blockspace.BlockSpace;
import sneer.bricks.snapps.blockspace.Bucket;

public class BlockSpaceImpl implements BlockSpace {

	@Override
	public Bucket localBucket() {
		return new BucketImpl();
		}

}

package sneer.bricks.hardwaresharing.dataspace.impl;

import sneer.bricks.hardwaresharing.dataspace.Bucket;
import sneer.bricks.hardwaresharing.dataspace.DataSpace;
import sneer.bricks.identity.seals.Seal;

public class DataSpaceImpl implements DataSpace {

	@Override
	public Bucket localBucket() {
		return new BucketImpl();
	}

	@Override
	public Bucket remoteBucketFor(Seal seal) {
		return new RemoteBucket(seal);
	}

}

package sneer.bricks.hardwaresharing.dataspace.buckets.impl;

import sneer.bricks.hardwaresharing.dataspace.buckets.Bucket;
import sneer.bricks.hardwaresharing.dataspace.buckets.Buckets;

public class BucketsImpl implements Buckets {

	@Override
	public Bucket produce() {
		return new BucketImpl();
	}

}

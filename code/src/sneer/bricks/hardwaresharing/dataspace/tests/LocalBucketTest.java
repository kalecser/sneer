package sneer.bricks.hardwaresharing.dataspace.tests;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardwaresharing.dataspace.Bucket;
import sneer.bricks.hardwaresharing.dataspace.DataSpace;

public class LocalBucketTest extends BucketTestBase {

	@Override
	protected Bucket subject() {
		return my(DataSpace.class).localBucket();
	}

}

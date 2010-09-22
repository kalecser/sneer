package sneer.bricks.snapps.blockspace.tests;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.snapps.blockspace.BlockSpace;
import sneer.bricks.snapps.blockspace.Bucket;

public class LocalBucketTest extends BucketTestBase {

	@Override
	protected Bucket subject() {
		return my(BlockSpace.class).localBucket();
	}

}

package sneer.bricks.hardwaresharing.dataspace.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Ignore;

import sneer.bricks.hardwaresharing.dataspace.Bucket;
import sneer.bricks.hardwaresharing.dataspace.DataSpace;
import sneer.bricks.identity.seals.Seal;

@Ignore
public class RemoteBucketTest extends BucketTestBase {

	@Override
	protected Bucket subject() {
		Seal seal = remoteSeal();
		return my(DataSpace.class).remoteBucketFor(seal);		
	}

}

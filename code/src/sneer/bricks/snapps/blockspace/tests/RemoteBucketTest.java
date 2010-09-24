package sneer.bricks.snapps.blockspace.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Ignore;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.snapps.blockspace.BlockSpace;
import sneer.bricks.snapps.blockspace.Bucket;

@Ignore
public class RemoteBucketTest extends BucketTestBase {

	@Override
	protected Bucket subject() {
		Seal seal = remoteSeal();
		return my(BlockSpace.class).remoteBucketBy(seal);		
	}

}

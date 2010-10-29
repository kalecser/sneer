package sneer.bricks.hardwaresharing.dataspace.buckets;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;

public class BlockRead extends Tuple {

	public final long blockNumber;

	public BlockRead(Seal seal, long blockNumber_) {
		super(seal);
		blockNumber = blockNumber_;
	}

	
}

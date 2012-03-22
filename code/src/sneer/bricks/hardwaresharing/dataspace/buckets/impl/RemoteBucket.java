package sneer.bricks.hardwaresharing.dataspace.buckets.impl;

import static basis.environments.Environments.my;
import basis.lang.Consumer;
import basis.util.concurrent.Latch;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardwaresharing.dataspace.buckets.BlockRead;
import sneer.bricks.hardwaresharing.dataspace.buckets.BlockReadResponse;
import sneer.bricks.hardwaresharing.dataspace.buckets.Bucket;
import sneer.bricks.identity.seals.Seal;

class RemoteBucket implements Bucket {

	private final Seal _seal;

	
	RemoteBucket(Seal seal) {
		_seal = seal;
	}

	
	@Override
	public byte[] read(final long blockNumber) {
		final byte[] result = null;
		my(TupleSpace.class).add(new BlockRead(_seal, blockNumber));

		final Latch latch = new Latch();
		
		WeakContract refToAvoidGC = my(TupleSpace.class).addSubscription(BlockReadResponse.class, new Consumer<BlockReadResponse>() { @Override public void consume(BlockReadResponse response) {
			if (!_seal.equals(response.publisher)) return;
			if (response.blockNumber != blockNumber) return;
				
			response.block.copyTo(result);
			latch.open();
		}});
		
		latch.waitTillOpen();
		refToAvoidGC.dispose();
		return result ;
	}

	
	@Override
	public void setSize(long sizeInBlocks) {
		throw new UnsupportedOperationException();
	}

	
	@Override
	public void write(long blockNumber, byte[] block) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

	
	@Override
	public void crash() {}

}

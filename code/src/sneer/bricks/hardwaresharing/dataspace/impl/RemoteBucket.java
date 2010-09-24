package sneer.bricks.hardwaresharing.dataspace.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardwaresharing.dataspace.BlockRead;
import sneer.bricks.hardwaresharing.dataspace.BlockReadResponse;
import sneer.bricks.hardwaresharing.dataspace.Bucket;
import sneer.bricks.identity.seals.Seal;
import sneer.foundation.lang.Consumer;

class RemoteBucket implements Bucket {

	private final Seal _seal;

	
	RemoteBucket(Seal seal) {
		_seal = seal;
	}

	
	@Override
	public byte[] read(final long blockNumber) {
		final byte[] result = null;
		my(TupleSpace.class).acquire(new BlockRead(_seal, blockNumber));

		final Latch latch = my(Latches.class).produce();
		
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
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	
	@Override
	public void crash() {}

}

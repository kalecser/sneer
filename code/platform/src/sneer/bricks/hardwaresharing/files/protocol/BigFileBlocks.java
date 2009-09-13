package sneer.bricks.hardwaresharing.files.protocol;

import sneer.bricks.hardware.ram.arrays.ImmutableArray;
import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.brickness.Tuple;
import sneer.foundation.environments.Environments;

public class BigFileBlocks extends Tuple {

	public static final int NUMBER_OF_BLOCKS = 10;
	public final ImmutableArray<Sneer1024> _contents;

	public BigFileBlocks(Sneer1024[] hash) {
		_contents = Environments.my(ImmutableArrays.class).newImmutableArray(hash);
	}

	public BigFileBlocks(ImmutableArray<Sneer1024> hash) {
		_contents = hash;
	}
	

}

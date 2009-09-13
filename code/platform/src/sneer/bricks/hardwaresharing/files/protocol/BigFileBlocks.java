package sneer.bricks.hardwaresharing.files.protocol;

import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.ram.arrays.ImmutableArray;
import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.brickness.Tuple;
import sneer.foundation.environments.Environments;

public class BigFileBlocks extends Tuple {

	public static final int NUMBER_OF_BLOCKS = 10;
	public final ImmutableArray<Sneer1024> _hash;

	public BigFileBlocks(Sneer1024[] hash) {
		_hash = Environments.my(ImmutableArrays.class).newImmutableArray(hash);
	}

	public BigFileBlocks(ImmutableArray<Sneer1024> hash) {
		_hash = hash;
	}

	public byte[] hashAsByteArray() {
		byte[][] hash = new byte[_hash.length()][];
		
		int current = 0;
		for (Sneer1024 sneer1024 : _hash){
			hash[current++] = sneer1024.bytes();
		}
		
		
		return Environments.my(Lang.class).arrays().merge(hash);
	}

	


	
}

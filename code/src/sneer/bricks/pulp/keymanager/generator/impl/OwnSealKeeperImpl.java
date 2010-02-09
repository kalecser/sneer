package sneer.bricks.pulp.keymanager.generator.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReferences;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.bricks.pulp.keymanager.generator.OwnSealKeeper;

class OwnSealKeeperImpl implements OwnSealKeeper {

	private final ImmutableReference<Seal> _ownSeal = my(ImmutableReferences.class).newInstance();
	
	
	@Override
	public boolean needsToProduceSeal() {
		return !_ownSeal.isAlreadySet();
	}

	
	@Override
	public void produceOwnSeal(byte[] randomness) {
		_ownSeal.set(new Seal(new ImmutableByteArray(randomness)));
	}

	
	@Override
	public Seal seal() {
		return _ownSeal.get();
	}
}

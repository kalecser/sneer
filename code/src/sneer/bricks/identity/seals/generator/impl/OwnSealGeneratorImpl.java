package sneer.bricks.identity.seals.generator.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReference;
import sneer.bricks.hardware.ram.ref.immutable.ImmutableReferences;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.generator.OwnSealGenerator;

class OwnSealGeneratorImpl implements OwnSealGenerator {

	private final ImmutableReference<Seal> _ownSeal = my(ImmutableReferences.class).newInstance();
	
	
	@Override
	public boolean needsToGenerateOwnSeal() {
		return !_ownSeal.isAlreadySet();
	}

	
	@Override
	public void generateOwnSeal(byte[] randomness) {
		_ownSeal.set(new Seal(new ImmutableByteArray(randomness)));
	}

	
	@Override
	public Seal generatedSeal() {
		return _ownSeal.get();
	}
}

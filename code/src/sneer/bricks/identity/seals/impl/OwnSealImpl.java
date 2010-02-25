package sneer.bricks.identity.seals.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Random;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.generator.OwnSealGenerator;

class OwnSealImpl implements OwnSeal {

	private Seal _ownSeal;
	

	@Override
	synchronized
	public Seal get() {
		if (_ownSeal == null)
			_ownSeal = produceOwnSeal();
		return _ownSeal;
	}

	
	private Seal produceOwnSeal() {
		if ("true".equals(System.getProperty("sneer.dummy")))
			return dummySeal();

		//All this complexity with a separate prevalent OwnSealKeeper is because the source of randomness cannot be inside a prevalent brick.
		if (my(OwnSealGenerator.class).needsToGenerateOwnSeal())
			my(OwnSealGenerator.class).generateOwnSeal(randomness());
		
		return my(OwnSealGenerator.class).generatedSeal();
	}


	private Seal dummySeal() {
		return new Seal(new ImmutableByteArray(new byte[128]));
	}


	private byte[] randomness() {
		my(Logger.class).log("This random source needs to be made cryptographically secure. " + getClass());

		byte[] result = new byte[128];
		new Random(System.nanoTime() + System.currentTimeMillis()).nextBytes(result);
		return result;
	}

}

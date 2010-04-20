package sneer.bricks.identity.seals.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Random;

import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.generator.OwnSealGenerator;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.arrays.ImmutableByteArray;

class OwnSealImpl implements OwnSeal {

	private final Register<Seal> _ownSeal = my(Signals.class).newRegister(null);

	@Override
	synchronized
	public Signal<Seal> get() {
		if (_ownSeal.output().currentValue() == null)
			_ownSeal.setter().consume(produceOwnSeal());
		return _ownSeal.output();
	}


	private Seal produceOwnSeal() {
		if ("true".equals(System.getProperty("sneer.dummy")))
			return dummySeal();

		//This complexity with a separate prevalent OwnSealGenerator is because the source of randomness cannot be inside a prevalent brick.
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

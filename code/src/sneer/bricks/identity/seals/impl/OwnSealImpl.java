package sneer.bricks.identity.seals.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.UnsupportedEncodingException;
import java.security.PublicKey;

import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.identity.keys.own.OwnKeys;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Functor;
import sneer.foundation.lang.arrays.ImmutableByteArray;

class OwnSealImpl implements OwnSeal {

	final Signal<Seal> _cache = init();
	
	
	@Override
	public Signal<Seal> get() {
		return _cache;
	}

	
	private Signal<Seal> init() {
		return adapt(my(OwnKeys.class).ownPublicKey());
	}


	private Signal<Seal> adapt(Signal<PublicKey> ownPublicKey) {
		return my(Signals.class).adapt(ownPublicKey, new Functor<PublicKey, Seal>() { @Override public Seal evaluate(PublicKey publicKey) {
			return publicKey == null
				? newTemporarySealForTests()
				: hash(publicKey);
		}});
	}

	
	private Seal hash(PublicKey publicKey) {
		Hash result = my(Crypto.class).digest(publicKey.getEncoded());
		return new Seal(new ImmutableByteArray(result.bytes.copy()));
	}

	
	private Seal newTemporarySealForTests() {
		try {
			Seal tmpSeal = new Seal(new ImmutableByteArray(Long.toHexString(System.nanoTime()).getBytes("UTF-8")));
			//my(BlinkingLights.class).turnOn(LightType.WARNING, "Public key not found. Created tmp seal for tests: " + tmpSeal, "As a real user, you should not be seeing this warning.");
			return tmpSeal;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

}

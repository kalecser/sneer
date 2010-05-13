package sneer.bricks.identity.keys.own.impl;

import static sneer.foundation.environments.Environments.my;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import sneer.bricks.hardware.cpu.codec.Codec;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.keys.own.OwnKeys;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;

class OwnKeysImpl implements OwnKeys {
	
	private final Register<PublicKey> _ownPublicKey = my(Signals.class).newRegister(null);
	@SuppressWarnings("unused")
	private PrivateKey _ownPrivateKey;

	
	@Override
	public void generateKeyPair(byte[] seed) {
		my(Logger.class).log("Generating key pair using seed: ", my(Codec.class).hex().encode(seed));
		
		KeyPair newPair = newKeyPair(seed);
		_ownPublicKey.setter().consume(newPair.getPublic());
		_ownPrivateKey = newPair.getPrivate();
	}


	private KeyPair newKeyPair(byte[] seed) {
		SecureRandom random = my(Crypto.class).newSecureRandom();
		random.setSeed(seed);
		
		KeyPairGenerator generator = my(Crypto.class).newKeyPairGeneratorForECDSA();
		generator.initialize(256, random);
		
		return generator.generateKeyPair();
	}

	
	@Override
	public Signal<PublicKey> ownPublicKey() {
		return _ownPublicKey.output();
	}

}

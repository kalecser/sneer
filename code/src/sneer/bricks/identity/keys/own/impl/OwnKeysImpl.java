package sneer.bricks.identity.keys.own.impl;

import static sneer.foundation.environments.Environments.my;

import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.identity.keys.own.OwnKeys;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;

class OwnKeysImpl implements OwnKeys {
	
	private static final Charset UTF8 = initUTF8();
	
	
	private final Register<PublicKey> _ownPublicKey = my(Signals.class).newRegister(null);
	@SuppressWarnings("unused")
	private PrivateKey _ownPrivateKey;

	
	@Override
	public void generateKeyPair(String passphrase) {
		KeyPair newPair = newKeyPair(passphrase);
		_ownPublicKey.setter().consume(newPair.getPublic());
		_ownPrivateKey = newPair.getPrivate();
	}


	private KeyPair newKeyPair(String passphrase) {
		SecureRandom random = my(Crypto.class).newSecureRandom();
		random.setSeed(passphrase.getBytes(UTF8));
		
		KeyPairGenerator generator = my(Crypto.class).newKeyPairGeneratorForECDSA();
		generator.initialize(256, random);
		
		return generator.generateKeyPair();
	}

	
	@Override
	public Signal<PublicKey> ownPublicKey() {
		return _ownPublicKey.output();
	}

	
	private static Charset initUTF8() {
		return Charset.forName("UTF-8");
	}

}

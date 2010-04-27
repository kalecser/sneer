package sneer.bricks.identity.keys.impl;

import static sneer.foundation.environments.Environments.my;

import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;

import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.identity.keys.Keys;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;

class KeysImpl implements Keys {
	
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

	
	@Override
	public boolean verifySignature(byte[] message, PublicKey publicKey,	byte[] signature) {
		Signature verifier;
		try {
			verifier = Signature.getInstance("SHA512WITHECDSA", "BC");
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		try {
			verifier.initVerify(publicKey);
			verifier.update(message);
			return verifier.verify(signature);
		} catch (Exception e) {
			return false;
		}
	}


	private static Charset initUTF8() {
		return Charset.forName("UTF-8");
	}

}

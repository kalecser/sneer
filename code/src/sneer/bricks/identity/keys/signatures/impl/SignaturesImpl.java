package sneer.bricks.identity.keys.signatures.impl;

import static sneer.foundation.environments.Environments.my;

import java.security.PublicKey;
import java.security.Signature;

import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.identity.keys.signatures.Signatures;

public class SignaturesImpl implements Signatures {
	
	@Override
	public boolean verifySignature(byte[] message, PublicKey publicKey,	byte[] signature) {
		Signature verifier;
		try {
			verifier = my(Crypto.class).getSHA512WithECDSA();
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

}

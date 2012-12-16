package sneer.bricks.hardware.cpu.crypto.ecdh.impl;

import static basis.environments.Environments.my;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;


import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.crypto.ecdh.ECDHKeyAgreement;
import sneer.bricks.identity.keys.own.OwnKeys;

class ECDHKeyAgreementImpl implements ECDHKeyAgreement {

	@Override
	public Hash generateSecret(byte[] peerPublicKey) {
		PublicKey otherPeerPublicKey = my(Crypto.class).unmarshalPublicKey(peerPublicKey);
		PrivateKey ownPrivateKey = my(OwnKeys.class).ownPrivateKey().currentValue();
		return my(Crypto.class).secretKeyFrom(otherPeerPublicKey, ownPrivateKey);
	}

	@Override
	public Hash generateSessionKey() {
		BigInteger sessionKey = new BigInteger(256, new SecureRandom());
		return my(Crypto.class).digest(sessionKey.toByteArray());
	}

}

package sneer.bricks.hardware.cpu.crypto.ecdh.impl;

import static basis.environments.Environments.my;

import java.security.PrivateKey;
import java.security.PublicKey;


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

}

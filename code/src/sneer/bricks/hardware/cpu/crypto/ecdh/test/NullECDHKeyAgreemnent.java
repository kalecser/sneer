package sneer.bricks.hardware.cpu.crypto.ecdh.test;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.crypto.ecdh.ECDHKeyAgreement;

public class NullECDHKeyAgreemnent implements ECDHKeyAgreement {

	@Override
	public Hash generateSecret(byte[] peerPublicKey) {
		return new Hash(new byte[] { 42 });
	}

}

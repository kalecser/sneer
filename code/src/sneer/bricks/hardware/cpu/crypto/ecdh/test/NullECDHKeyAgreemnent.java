package sneer.bricks.hardware.cpu.crypto.ecdh.test;

import java.util.Arrays;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.crypto.ecdh.ECDHKeyAgreement;

public class NullECDHKeyAgreemnent implements ECDHKeyAgreement {

	@Override
	public Hash generateSecret(byte[] peerPublicKey, byte[] sessionKey) {
		return hash();
	}

	private Hash hash() {
		byte[] ret = new byte[512/8];
		Arrays.fill(ret, (byte)42);
		return new Hash(ret);
	}

}

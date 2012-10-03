package sneer.bricks.hardware.cpu.crypto.ecdh.test;

import javax.crypto.SecretKey;

import sneer.bricks.hardware.cpu.crypto.ecdh.ECDHKeyAgreement;

public class NullECDHKeyAgreemnent implements ECDHKeyAgreement {

	@Override
	public SecretKey generateSecret(byte[] key) {
		return new SecretKey() {
			@Override public byte[] getEncoded() { return new byte[]{ 42 }; }
			@Override public String getFormat() { return null; }
			@Override public String getAlgorithm() { return null; }
		};
	}

}

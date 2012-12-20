package sneer.bricks.hardware.cpu.crypto.ecb.tests;

import sneer.bricks.hardware.cpu.crypto.ECBCipher;
import sneer.bricks.hardware.cpu.crypto.ecb.ECBCiphers;

public class NullECBCiphers implements ECBCiphers {

	@Override
	public ECBCipher newAES256(byte[] encryptKey, byte[] decryptKey) {
		return new ECBCipher() {
			@Override public byte[] encrypt(byte[] plainText) { return plainText; }
			@Override public byte[] decrypt(byte[] cipherText) { return cipherText; }
		};
	}

}

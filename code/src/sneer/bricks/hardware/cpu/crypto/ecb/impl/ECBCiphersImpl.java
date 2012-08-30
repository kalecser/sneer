package sneer.bricks.hardware.cpu.crypto.ecb.impl;

import static basis.environments.Environments.my;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.ECBCipher;
import sneer.bricks.hardware.cpu.crypto.ecb.ECBCiphers;

public class ECBCiphersImpl implements ECBCiphers {

	
	@Override
	public ECBCipher newAES256(byte[] key) {
		return my(Crypto.class).newAES256Cipher(key);
	}

	
}

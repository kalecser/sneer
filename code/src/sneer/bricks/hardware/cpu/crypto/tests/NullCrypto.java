package sneer.bricks.hardware.cpu.crypto.tests;

import java.io.File;
import java.security.KeyPair;
import java.security.Signature;

import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Digester;
import sneer.bricks.hardware.cpu.crypto.ECBCipher;
import sneer.bricks.hardware.cpu.crypto.Hash;

public class NullCrypto implements Crypto {
	
	
	@Override
	public Hash unmarshallHash(byte[] bytes) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}
	

	@Override
	public KeyPair newECDSAKeyPair(byte[] seed) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}
	

	@Override
	public Digester newDigester() {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}
	

	@Override
	public ECBCipher newAES256Cipher(byte[] key) {
		return new ECBCipher() {
			
			@Override
			public byte[] encrypt(byte[] plainText) {
				return plainText;
			}
			
			@Override
			public byte[] decrypt(byte[] cipherText) {
				return cipherText;
			}
		};
	}

	
	@Override
	public Signature getSHA512WithECDSA() {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

	
	@Override
	public Hash digest(File file) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

	
	@Override
	public Hash digest(byte[] input) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}
	
	
}
package sneer.bricks.hardware.cpu.crypto.impl;

import java.util.Arrays;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

import sneer.bricks.hardware.cpu.crypto.ECBCipher;

class ECBCipherImpl implements ECBCipher {
	
	private final BufferedBlockCipher encryptor;
	private final BufferedBlockCipher decrypter;
	
	ECBCipherImpl(byte[] key) {
		encryptor = newCipher(key, true);
		decrypter = newCipher(key, false);
	}
	
	@Override
	public byte[] encrypt(byte[] plainText) {
		synchronized (encryptor) {
			encryptor.reset();
			return process(encryptor, plainText);
		}
	}

	@Override
	public byte[] decrypt(byte[] cipherText) {
		synchronized (decrypter) {
			decrypter.reset();
			return process(decrypter, cipherText);
		}
	}
	
	static private BufferedBlockCipher newCipher(byte[] key, boolean forEncryption) {
		BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new AESEngine());
		cipher.init(forEncryption, new KeyParameter(key));
		
		return cipher;
	}
	
	static private byte[] process(BufferedBlockCipher cipher, byte[] data) {
		try {
			return tryToProcess(cipher, data);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	static private byte[] tryToProcess(BufferedBlockCipher cipher, byte[] data) throws Exception {
		byte[] ret = new byte[cipher.getOutputSize(data.length)];
		int retSize = cipher.processBytes(data, 0, data.length, ret, 0);
		retSize += cipher.doFinal(ret, retSize);
		
		return ret.length == retSize ? ret : Arrays.copyOf(ret, retSize);
	}

}

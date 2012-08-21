package spikes.neo.crypto;

import static java.lang.String.format;

import java.security.SecureRandom;
import java.util.Arrays;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.paddings.X923Padding;
import org.bouncycastle.crypto.params.KeyParameter;

public class CBCCrypto {
	
	public static void main(String[] args) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
		byte[] key = new byte[32];
		
		//byte[] message = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 };
		byte[] message = "Hey Neide".getBytes();
		print("Original", message);
		
		byte[] encrypted = encrypt(key, message);
		print("Encrypted", encrypted);
		
		byte[] decrypted = decrypt(key, encrypted);
		print("Decrypted", decrypted);
	}

	private static void print(String messageState, byte[] message) {
		System.out.println(format("%s message  => bytes%s size: %s string: %s", messageState, Arrays.toString(message), message.length, new String(message)));
	}

	private static byte[] encrypt(byte[] key, byte[] message) throws InvalidCipherTextException {
		BufferedBlockCipher cipher = newCipher(key, true);
		return process(cipher, message);
	}

	private static byte[] decrypt(byte[] key, byte[] data) throws InvalidCipherTextException {
		BufferedBlockCipher cipher = newCipher(key, false);
		return process(cipher, data);
	}
	
	private static BufferedBlockCipher newCipher(byte[] key, boolean encrypt) {
		X923Padding padding = new X923Padding();
		padding.init(new SecureRandom());
		
		BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new AESEngine(), padding);
		CipherParameters cipherKey = new KeyParameter(key);
		cipher.init(encrypt, cipherKey);
		
		return cipher;
	}

	private static byte[] process(BufferedBlockCipher cipher, byte[] data) throws InvalidCipherTextException {
		byte[] ret = new byte[cipher.getOutputSize(data.length)];
		int retSize = cipher.processBytes(data, 0, data.length, ret, 0);
		retSize += cipher.doFinal(ret, retSize);
		
		return Arrays.copyOf(ret, retSize);
	}

}

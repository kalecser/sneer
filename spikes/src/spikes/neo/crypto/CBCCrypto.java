package spikes.neo.crypto;

import static java.lang.String.format;

import java.util.Arrays;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

public class CBCCrypto {
	
	public static void main(String[] args) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
		byte[] key = new byte[32];
		
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
		PaddedBufferedBlockCipher cipher = newCipher(key, true);
		return cipher(cipher, message);
	}

	private static byte[] decrypt(byte[] key, byte[] data) throws InvalidCipherTextException {
		PaddedBufferedBlockCipher cipher = newCipher(key, false);
		return cipher(cipher, data);
	}
	
	private static PaddedBufferedBlockCipher newCipher(byte[] key, boolean encrypt) {
		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
		CipherParameters cipherKey = new KeyParameter(key);
		cipher.init(encrypt, cipherKey);
		
		return cipher;
	}

	private static byte[] cipher(PaddedBufferedBlockCipher cipher, byte[] data) throws InvalidCipherTextException {
		byte[] output = new byte[cipher.getOutputSize(data.length)];
		int cipherSize = cipher.processBytes(data, 0, data.length, output, 0);
		cipher.doFinal(output, cipherSize);
		
		return output;
	}

}

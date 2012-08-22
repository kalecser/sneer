package sneer.bricks.hardware.cpu.crypto;

public interface ECBCipher {
	
	byte[] encrypt(byte[] plainText);
	
	byte[] decrypt(byte[] cipherText);

}

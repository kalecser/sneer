package sneer.bricks.hardware.cpu.crypto;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;

import sneer.foundation.brickness.Brick;

@Brick
public interface Crypto {

	/**
	 * SHA512 
	 */
	Hash digest(byte[] input);

	Hash digest(File file) throws IOException;	

	Digester newDigester();

	Hash unmarshallHash(byte[] bytes);

	boolean verifySignature(byte[] message, PublicKey publicKey, byte[] signature);

}

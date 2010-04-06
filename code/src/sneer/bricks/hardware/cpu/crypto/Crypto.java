package sneer.bricks.hardware.cpu.crypto;

import java.io.File;
import java.io.IOException;

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

}

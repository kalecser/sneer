package sneer.bricks.hardware.cpu.crypto;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.Signature;

import sneer.foundation.brickness.Brick;

@Brick
public interface Crypto {

	Hash digest(byte[] input);
	Hash digest(File file) throws IOException;	

	Digester newDigester();

	Hash unmarshallHash(byte[] bytes);
	
	Signature getSHA512WithECDSA();
	KeyPair newECDSAKeyPair(byte[] seed);

}

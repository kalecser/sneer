package sneer.bricks.hardware.cpu.crypto;

import java.io.File;
import java.io.IOException;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;

import sneer.foundation.brickness.Brick;

@Brick
public interface Crypto {

	Hash digest(byte[] input);
	Hash digest(File file) throws IOException;	

	Digester newDigester();

	Hash unmarshallHash(byte[] bytes);
	
	SecureRandom newSecureRandom();
	
	KeyPairGenerator newKeyPairGeneratorForECDSA();
	
	Signature getSHA512WithECDSA();

}

package sneer.bricks.hardware.cpu.crypto.ecb;

import sneer.bricks.hardware.cpu.crypto.ECBCipher;
import basis.brickness.Brick;

@Brick
public interface ECBCiphers {

	ECBCipher newAES256(byte[] key);
	
}

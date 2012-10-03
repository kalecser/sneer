package sneer.bricks.hardware.cpu.crypto.ecdh;

import javax.crypto.SecretKey;

import basis.brickness.Brick;

@Brick
public interface ECDHKeyAgreement {

	SecretKey generateSecret(byte[] key);
	
}

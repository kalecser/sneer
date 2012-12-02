package sneer.bricks.hardware.cpu.crypto.ecdh;

import sneer.bricks.hardware.cpu.crypto.Hash;

import basis.brickness.Brick;

@Brick
public interface ECDHKeyAgreement {

	Hash generateSecret(byte[] key);
	
}

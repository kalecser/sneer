package sneer.bricks.hardware.cpu.crypto.ecdh;

import sneer.bricks.hardware.cpu.crypto.Hash;
import basis.brickness.Brick;

@Brick
public interface ECDHKeyAgreement {
	
	static final int SESSION_KEY_SIZE = 256/8;

	Hash generateSecret(byte[] peerPublicKey, byte[] sessionKey);

}

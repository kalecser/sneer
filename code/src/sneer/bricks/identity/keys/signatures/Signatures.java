package sneer.bricks.identity.keys.signatures;

import java.security.PublicKey;

import basis.brickness.Brick;


@Brick
public interface Signatures {
	
	boolean verifySignature(byte[] message, PublicKey publicKey, byte[] signature);

}

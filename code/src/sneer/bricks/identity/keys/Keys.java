package sneer.bricks.identity.keys;

import java.security.PublicKey;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick(Prevalent.class)
public interface Keys {

	void generateKeyPair(String passphraseSeed);
	
	Signal<PublicKey> ownPublicKey();

	boolean verifySignature(byte[] message, PublicKey publicKey, byte[] signature);

}

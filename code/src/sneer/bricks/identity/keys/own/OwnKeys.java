package sneer.bricks.identity.keys.own;

import java.security.PublicKey;

import basis.brickness.Brick;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.pulp.reactive.Signal;

@Brick(Prevalent.class)
public interface OwnKeys {
	
	int PUBLIC_KEY_SIZE_IN_BYTES = 91;

	void generateKeyPair(byte[] seed);
	
	Signal<PublicKey> ownPublicKey();

}

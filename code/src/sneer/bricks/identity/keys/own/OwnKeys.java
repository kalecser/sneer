package sneer.bricks.identity.keys.own;

import java.security.PublicKey;

import basis.brickness.Brick;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.pulp.reactive.Signal;

@Brick(Prevalent.class)
public interface OwnKeys {

	void generateKeyPair(byte[] seed);
	
	Signal<PublicKey> ownPublicKey();

}

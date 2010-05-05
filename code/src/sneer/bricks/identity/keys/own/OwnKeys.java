package sneer.bricks.identity.keys.own;

import java.security.PublicKey;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;

@Brick(Prevalent.class)
public interface OwnKeys {

	void generateKeyPair(byte[] seed);
	
	//showing git to leo
	
	Signal<PublicKey> ownPublicKey();

}

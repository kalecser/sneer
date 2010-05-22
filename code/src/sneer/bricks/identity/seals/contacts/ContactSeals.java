package sneer.bricks.identity.seals.contacts;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.exceptions.Refusal;

@Brick(Prevalent.class)
public interface ContactSeals {

	void put(String contactNickname, Seal seal) throws Refusal;

	Signal<Seal> sealGiven(Contact contact);
	Contact contactGiven(Seal peersSeal);
	Signal<String> nicknameGiven(Seal peersSeal);
}

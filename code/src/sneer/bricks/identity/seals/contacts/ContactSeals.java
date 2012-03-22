package sneer.bricks.identity.seals.contacts;

import basis.brickness.Brick;
import basis.lang.exceptions.Refusal;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;

@Brick(Prevalent.class)
public interface ContactSeals {

	void put(String contactNickname, Seal seal) throws Refusal;

	Signal<Seal> sealGiven(Contact contact);
	Contact contactGiven(Seal peersSeal);
	Signal<String> nicknameGiven(Seal peersSeal);
}

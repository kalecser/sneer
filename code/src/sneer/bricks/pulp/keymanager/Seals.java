package sneer.bricks.pulp.keymanager;

import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.social.contacts.Contact;
import sneer.foundation.brickness.Brick;

@Brick (Prevalent.class)
public interface Seals {

	Seal ownSeal();

	void put(String contactNickname, Seal seal);

	Seal sealGiven(Contact contact);
	Contact contactGiven(Seal peersSeal);
}

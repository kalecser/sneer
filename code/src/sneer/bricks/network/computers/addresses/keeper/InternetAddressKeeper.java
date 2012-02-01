package sneer.bricks.network.computers.addresses.keeper;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.exceptions.Refusal;

@Brick
public interface InternetAddressKeeper {

	SetSignal<InternetAddress> addresses();

	void put(Contact contact, String host, int port) throws Refusal;
	InternetAddress get(Contact contact);
	void remove(Contact contact);

}
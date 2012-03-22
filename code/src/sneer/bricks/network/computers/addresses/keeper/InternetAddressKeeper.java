package sneer.bricks.network.computers.addresses.keeper;

import basis.brickness.Brick;
import basis.lang.exceptions.Refusal;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.collections.SetSignal;

@Brick
public interface InternetAddressKeeper {

	SetSignal<InternetAddress> addresses();

	void put(Contact contact, String host, int port) throws Refusal;
	InternetAddress get(Contact contact);
	void remove(Contact contact);

}
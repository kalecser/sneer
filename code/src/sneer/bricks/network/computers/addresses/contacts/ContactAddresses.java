package sneer.bricks.network.computers.addresses.contacts;

import java.net.InetSocketAddress;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.Signal;
import basis.brickness.Brick;

@Brick
public interface ContactAddresses {

	Signal<InetSocketAddress> given(Contact contact);

}

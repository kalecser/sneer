package sneer.bricks.network.computers.addresses.dns;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.foundation.brickness.Brick;

@Brick
public interface Dns {

	ListSignal<String> knownIpsForContact(Contact contact);

}

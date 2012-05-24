package sneer.bricks.network.computers.udp.sightings;

import java.net.SocketAddress;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import basis.brickness.Brick;

@Brick
public interface SightingKeeper {

	void keep(Contact contact, SocketAddress sighting);
	
	SetSignal<SocketAddress> sightingsOf(Contact contact);
	
}

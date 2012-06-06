package sneer.bricks.network.computers.udp.sightings;

import java.net.InetSocketAddress;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import basis.brickness.Brick;

@Brick
public interface SightingKeeper {

	void keep(Contact contact, InetSocketAddress sighting);
	
	SetSignal<InetSocketAddress> sightingsOf(Contact contact);
	
}

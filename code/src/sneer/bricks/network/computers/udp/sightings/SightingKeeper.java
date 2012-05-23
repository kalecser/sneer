package sneer.bricks.network.computers.udp.sightings;

import java.net.SocketAddress;

import sneer.bricks.network.social.Contact;
import basis.brickness.Brick;

@Brick
public interface SightingKeeper {

	void put(Contact contact, SocketAddress sighting);
	
	SocketAddress get(Contact contact);
	
}

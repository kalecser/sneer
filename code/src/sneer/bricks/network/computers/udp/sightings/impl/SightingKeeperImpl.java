package sneer.bricks.network.computers.udp.sightings.impl;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sneer.bricks.network.computers.udp.sightings.SightingKeeper;
import sneer.bricks.network.social.Contact;

class SightingKeeperImpl implements SightingKeeper {
	
	private final Map<Contact, SocketAddress> addresses = new ConcurrentHashMap<Contact, SocketAddress>();

	@Override
	public void put(Contact contact, SocketAddress sighting) {
		addresses.put(contact, sighting);
	}

	@Override
	public SocketAddress get(Contact contact) {
		return addresses.get(contact);
	}

}

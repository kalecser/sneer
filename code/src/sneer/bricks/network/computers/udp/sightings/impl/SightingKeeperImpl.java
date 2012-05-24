package sneer.bricks.network.computers.udp.sightings.impl;

import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

import sneer.bricks.network.computers.udp.sightings.SightingKeeper;
import sneer.bricks.network.social.Contact;
import basis.lang.CacheMap;
import basis.lang.Producer;

class SightingKeeperImpl implements SightingKeeper {
	
	private static final SocketAddress[] SOCKET_ADDRESSES = new SocketAddress[0];
	private final CacheMap<Contact, Set<SocketAddress>> addresses = CacheMap.newInstance();
	private final static Producer<Set<SocketAddress>> newHashSet = new Producer<Set<SocketAddress>>() {  @Override public Set<SocketAddress> produce() {
		return new HashSet<SocketAddress>();
	}};

	@Override
	public void put(Contact contact, SocketAddress sighting) {
		getAddresses(contact).add(sighting);
	}

	@Override
	public SocketAddress[] get(Contact contact) {
		return getAddresses(contact).toArray(SOCKET_ADDRESSES);
	}

	private Set<SocketAddress> getAddresses(Contact contact) {
		return addresses.get(contact, newHashSet);
	}
	
}

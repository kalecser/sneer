package sneer.bricks.network.computers.udp.sightings.impl;

import static basis.environments.Environments.my;

import java.net.SocketAddress;

import sneer.bricks.network.computers.udp.sightings.SightingKeeper;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import basis.lang.CacheMap;
import basis.lang.Producer;

class SightingKeeperImpl implements SightingKeeper {
	
	private final CacheMap<Contact, SetRegister<SocketAddress>> addresses = CacheMap.newInstance();
	private final static Producer<SetRegister<SocketAddress>> newSetRegister = new Producer<SetRegister<SocketAddress>>() {  @Override public SetRegister<SocketAddress> produce() {
		return my(CollectionSignals.class).newSetRegister();
	}};

	@Override
	public void keep(Contact contact, SocketAddress sighting) {
		getAddresses(contact).add(sighting);
	}

	@Override
	public SetSignal<SocketAddress> sightingsOf(Contact contact) {
		return getAddresses(contact).output();
	}

	private SetRegister<SocketAddress> getAddresses(Contact contact) {
		return addresses.get(contact, newSetRegister);
	}
	
}

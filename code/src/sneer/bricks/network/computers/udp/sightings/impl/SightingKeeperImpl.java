package sneer.bricks.network.computers.udp.sightings.impl;

import static basis.environments.Environments.my;

import java.net.InetSocketAddress;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.remote.RemoteTuples;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.udp.sightings.SightingKeeper;
import sneer.bricks.network.computers.udp.sightings.UdpSighting;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import basis.lang.CacheMap;
import basis.lang.Consumer;
import basis.lang.Producer;

class SightingKeeperImpl implements SightingKeeper {
	
	static {
		my(TupleSpace.class).keep(UdpSighting.class);
	}
	
	
	private final CacheMap<Contact, SetRegister<InetSocketAddress>> addresses = CacheMap.newInstance();
	private final static Producer<SetRegister<InetSocketAddress>> newSetRegister = new Producer<SetRegister<InetSocketAddress>>() {  @Override public SetRegister<InetSocketAddress> produce() {
		return my(CollectionSignals.class).newSetRegister();
	}};
	
	
	@SuppressWarnings("unused")
	private final WeakContract refToAvoidGC = my(RemoteTuples.class).addSubscription(UdpSighting.class, new Consumer<UdpSighting>() { @Override public void consume(UdpSighting sighting) {
		Contact contact = my(ContactSeals.class).contactGiven(sighting.peerSeal);
		if (contact == null) return;
		keep(contact, new InetSocketAddress(sighting.host, sighting.port));
	}});
	

	@Override
	public void keep(Contact contact, InetSocketAddress sighting) {
		SetRegister<InetSocketAddress> addrs = getAddresses(contact);
		if (!addrs.output().currentContains(sighting)) {
			addrs.add(sighting);
			Seal seal = my(ContactSeals.class).sealGiven(contact).currentValue();
			my(TupleSpace.class).add(new UdpSighting(seal, sighting.getHostString(), sighting.getPort()));
		}
	}
	

	@Override
	public SetSignal<InetSocketAddress> sightingsOf(Contact contact) {
		return getAddresses(contact).output();
	}
	

	private SetRegister<InetSocketAddress> getAddresses(Contact contact) {
		return addresses.get(contact, newSetRegister);
	}
	
}

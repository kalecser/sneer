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
	
	
	private final CacheMap<Contact, SetRegister<InetSocketAddress>> sightingsByContact = CacheMap.newInstance();
	private final static Producer<SetRegister<InetSocketAddress>> newSetRegister = new Producer<SetRegister<InetSocketAddress>>() {  @Override public SetRegister<InetSocketAddress> produce() {
		return my(CollectionSignals.class).newSetRegister();
	}};
	
	
	@SuppressWarnings("unused")
	private final WeakContract refToAvoidGC = my(RemoteTuples.class).addSubscription(UdpSighting.class, new Consumer<UdpSighting>() { @Override public void consume(UdpSighting sighting) {
		Contact contact = my(ContactSeals.class).contactGiven(sighting.peerSeal);
		if (contact == null) return;
		addSighting(contact, new InetSocketAddress(sighting.host, sighting.port));
	}});
	

	@Override
	public void keep(Contact contact, InetSocketAddress sighting) {
		if (addSighting(contact, sighting)) 
			publishSighting(contact, sighting);
	}


	private void publishSighting(Contact contact, InetSocketAddress sighting) {
		Seal seal = my(ContactSeals.class).sealGiven(contact).currentValue();
		my(TupleSpace.class).add(new UdpSighting(seal, sighting.getHostString(), sighting.getPort()));
	}
	
	
	private boolean addSighting(Contact contact, InetSocketAddress sighting) {
		return sightingsRegisterOf(contact).add(sighting);
	}
	

	@Override
	public SetSignal<InetSocketAddress> sightingsOf(Contact contact) {
		return sightingsRegisterOf(contact).output();
	}
	

	private SetRegister<InetSocketAddress> sightingsRegisterOf(Contact contact) {
		return sightingsByContact.get(contact, newSetRegister);
	}
	
}
